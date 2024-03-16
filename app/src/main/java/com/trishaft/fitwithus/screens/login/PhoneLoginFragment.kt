package com.trishaft.fitwithus.screens.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.activities.MainActivity
import com.trishaft.fitwithus.communicators.IPhoneAuthenticatorCallbacks
import com.trishaft.fitwithus.databinding.FragmentPhoneLoginBinding
import com.trishaft.fitwithus.screens.signUp.performSingleClick
import com.trishaft.fitwithus.screens.signUp.toggleState
import com.trishaft.fitwithus.utilities.SnackBarManager
import com.trishaft.fitwithus.utilities.closeKeyboard
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.enableDisableScreen
import com.trishaft.fitwithus.utilities.enums.AuthExceptionStatus
import com.trishaft.fitwithus.utilities.exception
import com.trishaft.fitwithus.utilities.isValidMobileNumber
import com.trishaft.fitwithus.utilities.navigate
import com.trishaft.fitwithus.utilities.safe_args_modals.OTPArgs


class PhoneLoginFragment : Fragment(), IPhoneAuthenticatorCallbacks {


    private val binding: FragmentPhoneLoginBinding by lazy {
        FragmentPhoneLoginBinding.inflate(layoutInflater)
    }

    private val otpVerificationViewModel:OtpVerificationViewModel by lazy {
        ViewModelProvider(this)[OtpVerificationViewModel::class.java]
    }

    private val handler :Handler by lazy{
        Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        "onCreateView callback".debugLogs(javaClass.simpleName)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "onViewCreated callback".debugLogs(javaClass.simpleName)
        setUpClickListeners()
        setUpTextChangedListener()
    }

    private fun setUpTextChangedListener() {
        binding.apply {
            etMobile.doAfterTextChanged {
                it.toString().isValidMobileNumber { res ->
                    if (!res) {
                        etlMobile.error = requireContext().getString(R.string.valid_mobile_number)
                        btnShareOtp.toggleState(false)
                        return@isValidMobileNumber
                    }
                    etlMobile.error = null
                    btnShareOtp.toggleState(true)

                }
            }
        }
    }

    private fun setUpClickListeners() {
        binding.apply {
            backButton.setOnClickListener { navigate(R.id.action_phoneLoginFragment_to_loginFragment) }
            btnShareOtp.setOnClickListener { root.closeKeyboard(); validateAndRequestOtp() }

        }
    }


    private fun validateAndRequestOtp() {
        binding.btnShareOtp.performSingleClick(handler){
            enableDisableOperation(true)
            otpVerificationViewModel.sendToken(binding.etMobile.text.toString(), requireActivity(), this)
        }
    }

    /*
    *
    * These are the callbacks for the Phone Authentication Using Otp
    *
    * onPhoneVerificationDone --> When Verification is done
    * onPhoneVerificationCancelled --> when verification is failed
    * phoneOtpSharedSuccessFully --> when code is sent to user.
    * otpNotSharedSuccessFully --> otp is not shared for any reasons
    * */
    override fun onPhoneVerificationDone(credentials: PhoneAuthCredential) {
        "verification has been done successfully".debugLogs(javaClass.simpleName)
        "sms code is ${credentials.smsCode}".debugLogs(javaClass.simpleName)
    }

    override fun onPhoneVerificationCancelled(po: FirebaseException) {
        "code sharing process is failed".debugLogs(javaClass.simpleName)
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
        "code Has been shared successfully $codeShared".debugLogs(javaClass.simpleName)
        enableDisableOperation(false)
        SnackBarManager.getInstance().showSnackBar(
            MainActivity.getBinding().root,
            Snackbar.LENGTH_SHORT,
            requireContext().getString(R.string.code_sent)
        )
        navigateToVerifyOtp(codeShared, resendToken)
    }

    private fun navigateToVerifyOtp(
        codeShared: String,
        resendToken: PhoneAuthProvider.ForceResendingToken
    ) {
        findNavController().navigate(
            PhoneLoginFragmentDirections.actionPhoneLoginFragmentToOtpVerificationScreen(
                OTPArgs(
                    codeShared,
                    resendToken,
                    binding.etMobile.text.toString()
                )
            )
        )
    }


    override fun otpNotSharedSuccessFully() {
        "otp not shared for any reason".debugLogs(javaClass.simpleName)
        enableDisableOperation(false)
    }

    private fun enableDisableOperation(enableDisable: Boolean) {
        MainActivity.getInstance().window.enableDisableScreen(enableDisable)
        binding.progressBar.enableDisableScreen(enableDisable)
    }


}