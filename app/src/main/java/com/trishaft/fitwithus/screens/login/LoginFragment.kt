package com.trishaft.fitwithus.screens.login

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.activities.MainActivity
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.databinding.FragmentForgotBottomSheetBinding
import com.trishaft.fitwithus.databinding.FragmentLoginBinding
import com.trishaft.fitwithus.firebase.GoogleAuthenticationManager
import com.trishaft.fitwithus.firebase.RemoteConfigManager
import com.trishaft.fitwithus.otpReader.IOtpReader
import com.trishaft.fitwithus.screens.signUp.performSingleClick
import com.trishaft.fitwithus.screens.signUp.toggleState
import com.trishaft.fitwithus.screens.signUp.validate
import com.trishaft.fitwithus.screens.signUp.validateEmail
import com.trishaft.fitwithus.screens.signUp.validatePassword
import com.trishaft.fitwithus.utilities.EmailData
import com.trishaft.fitwithus.utilities.EmailSender
import com.trishaft.fitwithus.utilities.FitWithUsApplication
import com.trishaft.fitwithus.utilities.SnackBarManager
import com.trishaft.fitwithus.utilities.closeKeyboard
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.enableDisableScreen
import com.trishaft.fitwithus.utilities.isValidEmail
import com.trishaft.fitwithus.utilities.isValidMobileNumber
import com.trishaft.fitwithus.utilities.isValidPassword
import com.trishaft.fitwithus.utilities.navigate
import com.trishaft.fitwithus.utilities.showCustomDialog
import com.trishaft.fitwithus.utilities.startOnBackGroundThread
import com.trishaft.fitwithus.utilities.startOnMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginFragment : Fragment(), AuthenticationCallback, IOtpReader {


    companion object {

        private var credentials: Pair<String, String>? = null
        private var sharedOtp: String? = null
        private var userEntered: String? =
            null // this is used if somehow mail reference is lost then this will be called.

        /*
        * make the variable updated in the parallel communication or in multithreading as well.
        * */
        @Volatile
        var instance: LoginFragment? = null


        /*
        * run this method synchronized so that unnecessary instance is not created.
        * As all function call will always occur in serial manner.
        * */
    }


    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var googleInstance: GoogleAuthenticationManager? = null
    private var isAlreadySessionHere: GoogleSignInAccount? = null
    private var counter: CountDownTimer? = null

    private var bottomSheet: BottomSheetDialog? = null
    private var bBinding: FragmentForgotBottomSheetBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        setUpClickListeners()
        setUpBottomSheetDialog()
    }

    private fun setUpClickListeners() {
        binding.apply {
            tvForgotPassword.setOnClickListener {
                credentials = RemoteConfigManager.getRemoteConfigInstance()
                    .askForRemoteConfigKeys(requireActivity(), requireContext())
                bottomSheetForEmailSetup()
                showForgotPassword()
            }
            signUpNavigation.setOnClickListener { navigate(R.id.action_loginFragment_to_signUpFragment) }
            btnLogin.setOnClickListener {
                root.closeKeyboard()
                performSignIn()

            }
            btnMobile.setOnClickListener { navigate(R.id.action_loginFragment_to_phoneLoginFragment) }


            /*
            *  When click on the google button
            * */
            btnGoogle.setOnClickListener {
                handleGoogleClickListener()
            }

            /*
            *
            * When click on the email button
            * */


        }


    }

    private fun startTimer() {
        // 30 seconds in milliseconds, with a tick interval of 1 second
        counter = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                bBinding?.waitForTimer?.text = getString(
                    R.string.wait_for_20_second_resend_otp,
                    "${millisUntilFinished / 1000}"
                )
            }

            override fun onFinish() {
                requestOtpVisibility(View.GONE, View.VISIBLE)
            }
        }
    }

    private fun requestOtpVisibility(timer: Int, requestAgain: Int) {
        bBinding?.waitForTimer?.visibility = timer
        bBinding?.requestOtpAgain?.visibility = requestAgain
    }

    private fun performSignIn() {
        binding.btnLogin.performSingleClick(handler) {
            enableDisableOperation(true)
            loginViewModel.doEmailSignIn(
                binding.etEmail.text?.trim().toString(),
                binding.etPassword.text?.trim().toString(),
                this
            )
        }

    }


    private fun handleGoogleClickListener() {
        startOnBackGroundThread {
            googleInstance = GoogleAuthenticationManager.getTheGoogleInstance()
            isAlreadySessionHere =
                googleInstance?.checkWhetherUserHasAlreadyLoginTheAccount(requireContext())
            if (isAlreadySessionHere == null) {
                googleSignInLauncher.launch(
                    googleInstance?.showTheGoogleSignInToUser(
                        requireActivity()
                    )
                )
            } else {
                startOnMainThread {
                    requireContext().showCustomDialog(
                        layoutInflater,
                        isAlreadySessionHere?.email ?: getString(R.string.dummy_email),
                        getString(R.string.change_account),
                        getString(R.string.continue_with),
                        ::continueWithThisAccount
                    ) {
                        googleInstance?.removeOrChangeGoogleAccount(requireActivity())
                    }
                }
            }
        }
    }

    private fun continueWithThisAccount() {
        isAlreadySessionHere?.email?.debugLogs(javaClass.simpleName)
    }


    private fun setUpListeners() {
        binding.apply {

            /*
            * Applied the validation on email field
            * Password validation is not applied because on login it is not appropriate to show password validations.
            * */


            binding.apply {
                etEmail.doAfterTextChanged {
                    etEmail.validateEmail(requireContext(), etlEmail, btnLogin) {
                        btnLogin.validate(requireContext(), etEmail, etPassword)
                    }
                }

                etPassword.doAfterTextChanged {
                    etPassword.validatePassword(requireContext(), etlPassword, btnLogin) {
                        btnLogin.validate(requireContext(), etEmail, etPassword)
                    }
                }
            }

        }
    }


    /*bottom sheet handling*/

    private fun setUpBottomSheetDialog() {
        bottomSheet = BottomSheetDialog(requireContext())
        bBinding = FragmentForgotBottomSheetBinding.inflate(layoutInflater)
        if (bBinding?.root == null) return
        bottomSheet?.setContentView(bBinding!!.root)
        bBinding?.requestOtpAgain?.setOnClickListener {
            startTimer()
            counter?.start()
            requestOtpVisibility(View.VISIBLE, View.GONE)
            lifecycleScope.launch(Dispatchers.IO) {
                shareEmail { otp ->
                    sharedOtp = otp
                }
            }

        }

        bBinding?.confirmPassword?.doAfterTextChanged {
            if (it.toString().isValidPassword()) {
                bBinding?.confirmPasswordLayout?.error = null
                return@doAfterTextChanged
            }
            if(it.toString() != bBinding?.newPassword?.text.toString()){
                bBinding?.confirmPasswordLayout?.error = "password not match with new password"
                return@doAfterTextChanged
            }
            bBinding?.confirmPasswordLayout?.error =
                "password should contain a-z , A-Z , min 8 characters"

        }

        bBinding?.newPassword?.doAfterTextChanged {
            if (it.toString().isValidPassword()) {
                bBinding?.newPasswordLayout?.error = null
                return@doAfterTextChanged
            }
            bBinding?.newPasswordLayout?.error =
                "password should contain a-z , A-Z , min 8 characters"

        }

        /*
        * To release the resources when the bottomSheet is not visible
        * */


        bottomSheet?.apply {
            setOnDismissListener {
                counter?.cancel()
            }

            setOnShowListener {
                bBinding?.btnDone?.toggleState(true, bBinding?.bottomProgress)
                resetTheBottomSheet()
            }
        }
    }

    private fun resetTheBottomSheet() {
        bottomViewsVisibility(View.VISIBLE, View.GONE, View.GONE)
        bBinding?.btnDone?.text = getString(R.string.send_otp)
    }


    private fun bottomSheetForEmailSetup() {
        bBinding?.apply {
            bottomViewsVisibility(View.VISIBLE, View.GONE, View.GONE)
            forgotEmail.doAfterTextChanged {
                it.toString().isValidEmail { res ->
                    if (!res) {
                        forgotEmailLayout.error =
                            requireContext().getString(R.string.invalid_email_address)
                        btnDone.toggleState(false, null)
                        return@isValidEmail
                    }
                    forgotEmailLayout.error = null
                    btnDone.toggleState(true, bBinding?.bottomProgress)
                }
            }
            btnDone.setOnClickListener {
                btnDone.toggleState(false, bBinding?.bottomProgress)
                it.closeKeyboard()
                when (btnDone.text) {
                    getString(R.string.send_otp) -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            shareEmail { otp ->
                                sharedOtp = otp
                                bottomSheetForOtpSetup()
                            }
                        }
                    }

                    getString(R.string.verify_otp) -> {
                        if (sharedOtp == userEntered) {
                            bottomSheetForConfirmPasswordSetup()
                        } else {
                            it.toggleState(true, bBinding?.bottomProgress)
                            SnackBarManager.getInstance().showSnackBar(
                                bBinding?.root ?: binding.root,
                                BaseTransientBottomBar.LENGTH_LONG,
                                "Invalid Otp. please check this"
                            )
                        }
                    }

                    getString(R.string.change_password) -> {
                        confirmAndChangePassword()
                    }

                }


            }
            tryMoreMethods.setOnClickListener { bottomSheetForMobileSetup() }
        }
    }

    private fun confirmAndChangePassword() {

        bBinding?.apply {

            if (confirmPassword.text?.trim() == newPassword.text?.trim()) {
                btnDone.toggleState(true, bBinding?.bottomProgress)
                return
            }

            else if(!confirmPassword.text.toString().isValidPassword() || !newPassword.text.toString().isValidPassword()) {
                btnDone.toggleState(true, bBinding?.bottomProgress)
                SnackBarManager.getInstance().showSnackBar(
                    bBinding?.root ?: binding.root, BaseTransientBottomBar.LENGTH_LONG,
                    "new password and confirm password does not match"
                )
                return
            }
            else{
                btnDone.toggleState(true, bBinding?.bottomProgress)
                SnackBarManager.getInstance().showSnackBar(
                    bBinding?.root ?: binding.root, BaseTransientBottomBar.LENGTH_LONG,
                    "new password and confirm password does not match"
                )
            }
        }
    }

    private inline fun shareEmail(crossinline success: (String) -> Unit) {
        EmailSender.getInstance().sendEmail(
            requireContext(),
            EmailData(
                bBinding?.forgotEmail?.text?.trim().toString(),
                credentials?.first, credentials?.second
            )
        ) { isSuccess, errorMessage, otp ->
            startOnMainThread {
                bBinding?.btnDone?.toggleState(true, bBinding?.bottomProgress)
                if (isSuccess) {
                    otp?.let(success)
                } else {
                    SnackBarManager.getInstance().showSnackBar(
                        binding.root,
                        BaseTransientBottomBar.LENGTH_LONG,
                        errorMessage ?: ""
                    )
                }
            }
        }
    }


    private fun bottomSheetForOtpSetup() {
        startOnMainThread {
            bBinding?.apply {
                getString(R.string.verify_otp).also { btnDone.text = it }
                bottomViewsVisibility(View.GONE, View.GONE, View.VISIBLE)
                otpReader.registerCallbacks(this@LoginFragment)
            }


        }
    }

    private fun bottomSheetForConfirmPasswordSetup() {
        bBinding?.apply {
            btnDone.toggleState(true, bBinding?.bottomProgress)
            getString(R.string.change_password).also { btnDone.text = it }
            bottomViewsVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE)
        }
    }

    private fun bottomSheetForMobileSetup() {
        bBinding?.apply {
            bottomViewsVisibility(View.GONE, View.VISIBLE, View.GONE)
            forgotPhone.doAfterTextChanged {
                it.toString().isValidMobileNumber { res ->
                    if (!res) {
                        btnDone.toggleState(false, bBinding?.bottomProgress)
                        forgotPhoneLayout.error =
                            requireContext().getString(R.string.valid_mobile_number)
                        return@isValidMobileNumber
                    }
                    forgotPhoneLayout.error = null
                    btnDone.toggleState(true, bBinding?.bottomProgress)
                }

            }
            tryWithEmail.setOnClickListener { bottomSheetForEmailSetup() }
        }
    }

    private fun bottomViewsVisibility(
        email: Int,
        phone: Int,
        otp: Int,
        confirm: Int = View.GONE,
        timer: Int = View.GONE
    ) {
        bBinding?.apply {
            forgotEmailLayout.visibility = email
            tryMoreMethods.visibility = View.GONE
            tvEmailLabel.visibility = email
            forgotPhoneLayout.visibility = phone
            tryWithEmail.visibility = View.GONE
            tvForgotPhone.visibility = phone
            otpReader.visibility = otp
            requestOtpAgain.visibility = otp
            tvNewPassword.visibility = confirm
            tvConfirmPassword.visibility = confirm
            newPasswordLayout.visibility = confirm
            confirmPasswordLayout.visibility = confirm
            waitForTimer.visibility = timer
        }
    }

    private fun showForgotPassword() {
        if (bottomSheet?.isShowing == true) {
            return
        }
        bottomSheet?.show()
    }


    /*
    * Callbacks function
    *
    *
    * */
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data.toString().debugLogs(javaClass.simpleName)
                result.data?.data?.userInfo?.debugLogs(javaClass.simpleName)
            }
        }

    private fun enableDisableOperation(enableDisable: Boolean) {
        MainActivity.getInstance().window.enableDisableScreen(enableDisable)
        binding.progressBar.enableDisableScreen(enableDisable)
    }

    override fun onSuccessfulAuthorization(user: FirebaseUser?) {
        enableDisableOperation(false)
        SnackBarManager.getInstance().showSnackBar(
            MainActivity.getBinding().root,
            Snackbar.LENGTH_SHORT,
            "Login Successful"
        ).toString()
    }

    override fun onFailedAuthorization(error: String) {
        "onFailure $error".debugLogs(javaClass.name)
        SnackBarManager.getInstance()
            .showSnackBar(MainActivity.getBinding().root, Snackbar.LENGTH_SHORT, error).toString()
        enableDisableOperation(false)
    }

    override fun onAuthorizationCanceled() {
        SnackBarManager.getInstance().showSnackBar(
            MainActivity.getBinding().root,
            Snackbar.LENGTH_SHORT,
            "operation canceled"
        ).toString()
    }

    override fun onAuthorizationComplete(task: Task<AuthResult>) {
        "onAuth Complete $task".debugLogs(javaClass.name)
    }


    /*These are the callbacks for the otp */
    override fun userEnteredOtp(otp: String) {
        FitWithUsApplication.noImplementationLog(requireContext())
    }

    override fun invalidOtp(errorMessage: String, otp: String) {
        FitWithUsApplication.noImplementationLog(requireContext())
    }

    override fun verifyOtp(otp: String) {
        userEntered = otp
    }
}