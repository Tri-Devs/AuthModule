package com.trishaft.fitwithus

import android.app.Activity
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.communicators.IPhoneAuthenticatorCallbacks
import com.trishaft.fitwithus.firebase.EmailAuthenticationManager
import com.trishaft.fitwithus.firebase.PhoneAuthManager

class AuthRepo {

    companion object {
        private var repoInstance: AuthRepo? = null
        fun getInstance() = repoInstance ?: AuthRepo().also { repoInstance = it }
    }

    private var emailAuthManager: EmailAuthenticationManager? = null
    private var phoneAuthManager: PhoneAuthManager? = null

    init {
        initAuthManagers()
    }

    /**
     *  function to initialize the different auth managers.
     */
    private fun initAuthManagers() {
        phoneAuthManager = PhoneAuthManager.getInstance()
        emailAuthManager = EmailAuthenticationManager.getInstance()
    }

    suspend fun doEmailSignUp(email:String,password:String, listener:AuthenticationCallback){
        emailAuthManager?.doSignUp(email,password,listener)
    }


    suspend fun doEmailSignIn(email:String,password:String, listener:AuthenticationCallback){
        emailAuthManager?.doSignIn(email,password,listener)
    }


    suspend fun doPhoneVerification(otp:String, otpToken:String,listener: AuthenticationCallback){
        phoneAuthManager?.verifyUserUsingOTP(otp,otpToken, listener)
    }

    suspend fun setUpPhoneAuthentication(phoneNumber:String,activity: Activity, listener:IPhoneAuthenticatorCallbacks){
        phoneAuthManager?.setUpPhoneAuthenticator(phoneNumber, activity, listener)
    }

    suspend fun setUpResendTokenAuthentication(phoneNumber:String,activity: Activity, resendToken:ForceResendingToken,listener:IPhoneAuthenticatorCallbacks){
        phoneAuthManager?.setUpPhoneAuthenticator(phoneNumber, activity, resendToken,listener)
    }

}