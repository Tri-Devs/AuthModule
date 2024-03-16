package com.trishaft.fitwithus.screens.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.activities.MainActivity
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.communicators.IPhoneAuthenticatorCallbacks
import com.trishaft.fitwithus.databinding.FragmentOtpVerificationScreenBinding
import com.trishaft.fitwithus.otpReader.IOtpReader
import com.trishaft.fitwithus.screens.signUp.performSingleClick
import com.trishaft.fitwithus.utilities.SnackBarManager
import com.trishaft.fitwithus.utilities.closeKeyboard
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.enableDisableScreen
import com.trishaft.fitwithus.utilities.enums.AuthExceptionStatus
import com.trishaft.fitwithus.utilities.exception
import com.trishaft.fitwithus.utilities.navigate
import com.trishaft.fitwithus.utilities.safe_args_modals.OTPArgs


class OtpVerificationScreen : Fragment(), IOtpReader, AuthenticationCallback {

    private val binding: FragmentOtpVerificationScreenBinding by lazy {
        FragmentOtpVerificationScreenBinding.inflate(layoutInflater)
    }

    private val otpVerificationViewModel: OtpVerificationViewModel by lazy {
        ViewModelProvider(this)[OtpVerificationViewModel::class.java]
    }

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var args: OtpVerificationScreenArgs? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args = OtpVerificationScreenArgs.fromBundle(requireArguments())
        "${args?.otpToken?.otpToken}".debugLogs(javaClass.simpleName)
        "${args?.otpToken?.resendToken}".debugLogs(javaClass.simpleName)
        "${args?.otpToken?.phoneNumber}".debugLogs(javaClass.simpleName)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpReader.registerCallbacks(this)
        initPhoneNumber()
        setUpClickListeners()
    }

    /**
     *  function to the number
     */
    private fun initPhoneNumber() {
        binding.userNumber.text = buildString {
            append(requireContext().getString(R.string.phone_code))
            append(args?.otpToken?.phoneNumber)
        }
    }

    private fun setUpClickListeners() {
        binding.apply {
            backButton.setOnClickListener { navigate(R.id.action_otpVerificationScreen_to_phoneLoginFragment) }
            tryMoreMethods.setOnClickListener { navigate(R.id.action_otpVerificationScreen_to_loginFragment) }
            notReceiveOtp.setOnClickListener { resendOtp() }
        }
    }

    private fun resendOtp() {
        binding.notReceiveOtp.performSingleClick(handler) {
            args?.otpToken?.let {
                enableDisableOperation(true)
                performResendTokenOperation(it)
            }
        }
    }

    private fun performResendTokenOperation(args: OTPArgs) {
        otpVerificationViewModel.getResendToken(args.phoneNumber, requireActivity(),
            args.resendToken, object : IPhoneAuthenticatorCallbacks {
                override fun onPhoneVerificationDone(credentials: PhoneAuthCredential) {
                }

                override fun onPhoneVerificationCancelled(po: FirebaseException) {
                    enableDisableOperation(false)
                    SnackBarManager.getInstance().showSnackBar(
                        MainActivity.getBinding().root,
                        Snackbar.LENGTH_SHORT,
                        po.exception(
                            requireContext(),
                            AuthExceptionStatus.OTP
                        )
                    ).toString()
                    po.printStackTrace()
                }

                override fun phoneOtpSharedSuccessFully(
                    codeShared: String,
                    resendToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    enableDisableOperation(false)
                    SnackBarManager.getInstance().showSnackBar(
                        MainActivity.getBinding().root,
                        Snackbar.LENGTH_SHORT,
                        requireContext().getString(R.string.code_sent)
                    ).toString()
                }

                override fun otpNotSharedSuccessFully() {
                    enableDisableOperation(false)
                }

            })
    }

    override fun userEnteredOtp(otp: String) {
        Log.d("otpLogger", otp)
    }


    override fun invalidOtp(errorMessage: String, otp: String) {
        "invalidOtp $otp".debugLogs(javaClass.simpleName)
    }

    override fun verifyOtp(otp: String) {
        "userEnteredOtp $otp".debugLogs(javaClass.simpleName)
        args?.otpToken?.otpToken?.let {
            binding.root.closeKeyboard()
            enableDisableOperation(true)
            otpVerificationViewModel.verifyOtp(otp, it, this)
        }
    }

    private fun enableDisableOperation(enableDisable: Boolean) {
        MainActivity.getInstance().window.enableDisableScreen(enableDisable)
        binding.progressBar.enableDisableScreen(enableDisable)
    }

    override fun onSuccessfulAuthorization(user: FirebaseUser?) {
        enableDisableOperation(false)
        "onSuccessfulAuthorization callback".debugLogs(javaClass.name)

        SnackBarManager.getInstance().showSnackBar(
            MainActivity.getBinding().root,
            Snackbar.LENGTH_SHORT,
            requireContext().getString(R.string.success_sign_up)
        ).toString()

    }

    override fun onFailedAuthorization(error: Exception) {
        enableDisableOperation(false)
        "onFailedAuthorization callback".debugLogs(javaClass.name)
        SnackBarManager.getInstance().showSnackBar(
            MainActivity.getBinding().root,
            Snackbar.LENGTH_SHORT,
            error.exception(
                requireContext(), AuthExceptionStatus.OTP
            )
        ).toString()
    }

    override fun onAuthorizationCanceled() {
        enableDisableOperation(false)
        "onAuthorizationCanceled callback".debugLogs(javaClass.name)
    }

    override fun onAuthorizationComplete(task: Task<AuthResult>) {
        "onAuthorizationComplete callback".debugLogs(javaClass.name)
    }
}