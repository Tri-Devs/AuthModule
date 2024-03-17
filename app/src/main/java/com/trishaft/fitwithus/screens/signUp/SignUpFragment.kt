package com.trishaft.fitwithus.screens.signUp

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.activities.MainActivity
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.databinding.FragmentSignUpBinding
import com.trishaft.fitwithus.firebase.GoogleAuthenticationManager
import com.trishaft.fitwithus.utilities.Constants
import com.trishaft.fitwithus.utilities.SnackBarManager
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.enableDisableScreen
import com.trishaft.fitwithus.utilities.navigate
import com.trishaft.fitwithus.utilities.showCustomDialog
import com.trishaft.fitwithus.utilities.startOnBackGroundThread
import com.trishaft.fitwithus.utilities.startOnMainThread


class SignUpFragment : Fragment(), AuthenticationCallback {

    private val binding: FragmentSignUpBinding by lazy {
        FragmentSignUpBinding.inflate(layoutInflater)
    }

    private val signUpViewModel:SignUpViewModel by lazy {
        ViewModelProvider(this)[SignUpViewModel::class.java]
    }

    private lateinit var handler: Handler

    private var googleInstance : GoogleAuthenticationManager? = null
    private var isAlreadySessionHere : GoogleSignInAccount? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       if(savedInstanceState != null){
           binding.etEmail.setText(savedInstanceState.getString(Constants.USER_EMAIL))
           binding.etPassword.setText(savedInstanceState.getString(Constants.USER_PASSWORD))
       }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        setTextChangedListeners()
        setListeners()
    }

    private fun setTextChangedListeners() {
        binding.apply {
            etEmail.doAfterTextChanged {
                etEmail.validateEmail(requireContext(), etlEmail, btnSignUp) {
                    btnSignUp.validate(requireContext(), etEmail, etPassword)
                }
            }

            etPassword.doAfterTextChanged {
                etPassword.validatePassword(requireContext(), etlPassword, btnSignUp) {
                    btnSignUp.validate(requireContext(), etEmail, etPassword)
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnSignUp.setOnClickListener { performSignUp() }
            btnGoogle.setOnClickListener { performGoogleSignIn() }
            btnMobile.setOnClickListener { performPhoneAuthentication() }
            signUpNavigation.setOnClickListener { navigate(R.id.action_signUpFragment_to_loginFragment) }
            rememberMe.setOnCheckedChangeListener { compoundButton, switchState ->
                saveLastLoginProfile(compoundButton, switchState)
            }
        }
    }

    private fun saveLastLoginProfile(compoundButton: CompoundButton?, switchState: Boolean) {

    }

    private fun performPhoneAuthentication() {
        binding.btnMobile.performSingleClick(handler) {
            navigate(R.id.action_signUpFragment_to_phoneLoginFragment)
        }
    }

    private fun performGoogleSignIn() {
        binding.btnGoogle.performSingleClick(handler) {
            handleGoogleClickListener()
        }
    }



    private fun performSignUp() {
        binding.btnSignUp.performSingleClick(handler) {
            enableDisableOperation(true)
            doSignUp()
        }
    }


    private fun doSignUp() {
        signUpViewModel.doEmailSignUp(
            binding.etEmail.text?.trim().toString(),
            binding.etPassword.text?.trim().toString(), this
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.USER_EMAIL, binding.etEmail.text.toString())
        outState.putString(Constants.USER_PASSWORD, binding.etPassword.text.toString())
    }

    private fun enableDisableOperation(enableDisable: Boolean) {
        MainActivity.getInstance().window.enableDisableScreen(enableDisable)
        binding.progressBar.enableDisableScreen(enableDisable)
    }

    private fun handleGoogleClickListener() {
        startOnBackGroundThread{
            googleInstance = GoogleAuthenticationManager.getTheGoogleInstance()
            isAlreadySessionHere = googleInstance?.checkWhetherUserHasAlreadyLoginTheAccount(MainActivity.getInstance())
            if (isAlreadySessionHere == null) {
                googleSignInLauncher.launch(googleInstance?.showTheGoogleSignInToUser(MainActivity.getInstance()))
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



    override fun onSuccessfulAuthorization(user: FirebaseUser?) {

        "onSuccess ${user?.displayName}".debugLogs(javaClass.simpleName)
        "onSuccess ${user?.email}".debugLogs(javaClass.simpleName)

        enableDisableOperation(false)
        SnackBarManager.getInstance().showSnackBar(
            MainActivity.getBinding().root,
            Snackbar.LENGTH_SHORT,
            "your account is successfully registered."
        ).toString()
    }

    override fun onFailedAuthorization(error: String) {
        "onFailure $error".debugLogs(javaClass.simpleName)

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
        "onAuth Complete $task".debugLogs(javaClass.simpleName)
    }

}
