package com.trishaft.fitwithus.screens.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.databinding.FragmentOtpVerificationScreenBinding
import com.trishaft.fitwithus.otpReader.IOtpReader
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.navigate


class OtpVerificationScreen : Fragment() , IOtpReader{

    private val binding : FragmentOtpVerificationScreenBinding by lazy {
        FragmentOtpVerificationScreenBinding.inflate(layoutInflater)
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
        binding.otpReader.registerCallbacks(this)
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.apply {
            backButton.setOnClickListener { navigate(R.id.action_otpVerificationScreen_to_phoneLoginFragment) }
            tryMoreMethods.setOnClickListener { navigate(R.id.action_otpVerificationScreen_to_loginFragment)}
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtpVerificationScreen()
    }

    override fun userEnteredOtp(otp: String) {
       "userEnteredOtp $otp".debugLogs(javaClass.simpleName)
    }

    override fun invalidOtp(errorMessage: String, otp: String) {
        "invalidOtp $otp".debugLogs(javaClass.simpleName)
    }
}