package com.trishaft.fitwithus.screens.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.databinding.FragmentForgotBottomSheetBinding
import com.trishaft.fitwithus.databinding.FragmentLoginBinding
import com.trishaft.fitwithus.firebase.GoogleAuthenticationManager
import com.trishaft.fitwithus.utilities.SnackBarManager
import com.trishaft.fitwithus.utilities.closeKeyboard
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.isValidEmail
import com.trishaft.fitwithus.utilities.isValidMobileNumber
import com.trishaft.fitwithus.utilities.navigate
import com.trishaft.fitwithus.utilities.showCustomDialog
import com.trishaft.fitwithus.utilities.startOnBackGroundThread
import com.trishaft.fitwithus.utilities.startOnMainThread


class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    private var googleInstance : GoogleAuthenticationManager? = null
    private var isAlreadySessionHere : GoogleSignInAccount ? = null

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
            tvForgotPassword.setOnClickListener { bottomSheetForEmailSetup(); showForgotPassword() }
            signUpNavigation.setOnClickListener { navigate(R.id.action_loginFragment_to_signUpFragment) }
            btnLogin.setOnClickListener {
                root.closeKeyboard()
                SnackBarManager.getInstance().showSnackBar(
                    root,
                    BaseTransientBottomBar.LENGTH_LONG,
                    getString(R.string.invalid_email_address)
                )
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


    private fun handleGoogleClickListener() {
        startOnBackGroundThread{
            googleInstance = GoogleAuthenticationManager.getTheGoogleInstance(requireContext())
             isAlreadySessionHere = googleInstance?.checkWhetherUserHasAlreadyLoginTheAccount(requireContext())
            if (isAlreadySessionHere == null) {
                googleSignInLauncher.launch(googleInstance?.showTheGoogleSignInToUser(requireActivity()))
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

    private fun continueWithThisAccount(){
        isAlreadySessionHere?.email?.debugLogs(javaClass.simpleName)
    }


    private fun setUpListeners() {
        binding.apply {

            /*
            * Applied the validation on email field
            * Password validation is not applied because on login it is not appropriate to show password validations.
            * */
            etEmail.doAfterTextChanged {
                if (it.toString().isValidEmail()) {
                    etlEmail.error = null // Clear error if email is valid
                } else {
                    etlEmail.error = getString(R.string.invalid_email_address)
                }
            }

        }
    }


    companion object {

        /*
        * make the variable updated in the parallel communication or in multithreading as well.
        * */
        @Volatile
        var instance: LoginFragment? = null


        /*
        * run this method synchronized so that unnecessary instance is not created.
        * As all function call will always occur in serial manner.
        * */
        @JvmStatic
        fun loginNewInstance(): LoginFragment {
            return instance ?: synchronized(this) {
                instance ?: LoginFragment().also { instance = it }
            }
        }
    }


    /*bottom sheet handling*/

    private fun setUpBottomSheetDialog() {
        bottomSheet = BottomSheetDialog(requireContext())
        bBinding = FragmentForgotBottomSheetBinding.inflate(layoutInflater)
        if (bBinding?.root == null) return
        bottomSheet?.setContentView(bBinding!!.root)
    }

    private fun bottomSheetForEmailSetup() {
        bBinding?.apply {
            bottomViewsVisibility(View.VISIBLE, View.GONE, View.GONE)
            forgotEmail.doAfterTextChanged {
                if (it.toString().isValidEmail()) {
                    forgotEmailLayout.error = null // Clear error if email is valid
                } else {
                    forgotEmailLayout.error = getString(R.string.invalid_email_address)
                }
            }
            btnDone.setOnClickListener {
                bottomSheetForOtpSetup()
            }
            tryMoreMethods.setOnClickListener { bottomSheetForMobileSetup() }
        }
    }

    private fun bottomSheetForOtpSetup() {
        bBinding?.apply {
            getString(R.string.verify_otp).also { btnDone.text = it }
            bottomViewsVisibility(View.GONE, View.GONE, View.VISIBLE)
            btnDone.setOnClickListener {
                bottomSheetForConfirmPasswordSetup()
            }
        }

    }

    private fun bottomSheetForConfirmPasswordSetup() {
        bBinding?.apply {
            getString(R.string.change_password).also { btnDone.text = it }
            bottomViewsVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE)

        }
    }

    private fun bottomSheetForMobileSetup() {
        bBinding?.apply {
            bottomViewsVisibility(View.GONE, View.VISIBLE, View.GONE)
            forgotPhone.doAfterTextChanged {
                if (it.toString().isValidMobileNumber()) {
                    forgotPhoneLayout.error = null // Clear error if email is valid
                } else {
                    forgotPhoneLayout.error =
                        getString(R.string.mobile_number_should_contain_10_digits)
                }
            }
            tryWithEmail.setOnClickListener { bottomSheetForEmailSetup() }
        }
    }

    private fun bottomViewsVisibility(email: Int, phone: Int, otp: Int, confirm: Int = View.GONE) {
        bBinding?.apply {
            forgotEmailLayout.visibility = email
            tryMoreMethods.visibility = email
            tvEmailLabel.visibility = email
            forgotPhoneLayout.visibility = phone
            tryWithEmail.visibility = phone
            tvForgotPhone.visibility = phone
            otpReader.visibility = otp
            requestOtpAgain.visibility = otp
            tvNewPassword.visibility = confirm
            tvConfirmPassword.visibility = confirm
            newPasswordLayout.visibility = confirm
            confirmPasswordLayout.visibility = confirm
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
}