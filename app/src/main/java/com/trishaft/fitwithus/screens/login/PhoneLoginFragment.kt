package com.trishaft.fitwithus.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.PhoneAuthCredential
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.communicators.IPhoneAuthenticatorCallbacks
import com.trishaft.fitwithus.databinding.FragmentPhoneLoginBinding
import com.trishaft.fitwithus.firebase.PhoneAuthManager
import com.trishaft.fitwithus.utilities.closeKeyboard
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.isValidMobileNumber
import com.trishaft.fitwithus.utilities.navigate


class PhoneLoginFragment : Fragment(), IPhoneAuthenticatorCallbacks {


    private val binding: FragmentPhoneLoginBinding by lazy {
        FragmentPhoneLoginBinding.inflate(layoutInflater)
    }

    private val phoneAuthManager: PhoneAuthManager by lazy {
        PhoneAuthManager().getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.apply {
            backButton.setOnClickListener { navigate(R.id.action_phoneLoginFragment_to_loginFragment) }
            btnShareOtp.setOnClickListener { root.closeKeyboard() ; validateAndRequestOtp() }
        }
    }


    private fun validateAndRequestOtp() {
        val mobileNumber = binding.etMobile.text.toString()
        if (mobileNumber.isValidMobileNumber()) {

            navigate(R.id.action_phoneLoginFragment_to_otpVerificationScreen)
            // phoneAuthManager.setUpPhoneAuthenticator(mobileNumber, requireActivity(), this)
        }
        else{
            binding.etlMobile.error = getString(R.string.valid_mobile_number)
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
        "verification has been done successfully".debugLogs("PhoneAuthenticator")
    }

    override fun onPhoneVerificationCancelled() {
        "code sharing process is failed".debugLogs("PhoneAuthenticator")
    }

    override fun phoneOtpSharedSuccessFully(codeShared: String) {
        "code Has been shared successfully".debugLogs("PhoneAuthenticator")
    }

    override fun otpNotSharedSuccessFully() {
        "otp not shared for any reason".debugLogs("PhoneAuthenticator")
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhoneLoginFragment().apply {

            }
    }
}