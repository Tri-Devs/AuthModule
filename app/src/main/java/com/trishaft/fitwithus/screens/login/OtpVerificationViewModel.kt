package com.trishaft.fitwithus.screens.login

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.trishaft.fitwithus.AuthRepo
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.communicators.IPhoneAuthenticatorCallbacks
import kotlinx.coroutines.launch

class OtpVerificationViewModel(application: Application):AndroidViewModel(application) {
    private lateinit var authRepo: AuthRepo

    init {
        initAuthRepo()
    }

    private fun initAuthRepo() {
        authRepo = AuthRepo.getInstance()
    }

    fun verifyOtp(otp:String, otpToken:String, listener:AuthenticationCallback){
        viewModelScope.launch {
            authRepo.doPhoneVerification(otp,otpToken,listener)
        }
    }

    fun sendToken(phoneNumber:String,activity:Activity, listener:IPhoneAuthenticatorCallbacks){
        viewModelScope.launch {
            authRepo.setUpPhoneAuthentication(phoneNumber, activity,listener)
        }
    }

    fun getResendToken(phoneNumber:String,activity:Activity,resendToken:ForceResendingToken, listener:IPhoneAuthenticatorCallbacks){
        viewModelScope.launch {
            authRepo.setUpResendTokenAuthentication(phoneNumber, activity, resendToken,listener)
        }
    }
}