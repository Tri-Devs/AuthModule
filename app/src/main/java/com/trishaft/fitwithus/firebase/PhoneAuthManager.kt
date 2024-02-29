package com.trishaft.fitwithus.firebase

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.trishaft.fitwithus.communicators.IPhoneAuthenticatorCallbacks
import com.trishaft.fitwithus.utilities.FitWithUsApplication
import com.trishaft.fitwithus.utilities.debugLogs
import java.util.concurrent.TimeUnit

class PhoneAuthManager {

    private var listener: IPhoneAuthenticatorCallbacks? = null
    private var instance : PhoneAuthManager? = null


    fun getInstance() : PhoneAuthManager {
        return instance?: PhoneAuthManager().also { instance = it }
    }


        public fun setUpPhoneAuthenticator(
            phoneNumber: String,
            activity: Activity,
            parentReference: IPhoneAuthenticatorCallbacks
        ) {
            this.listener = parentReference
            PhoneAuthProvider.verifyPhoneNumber(setUpOptions(phoneNumber, activity))
        }



    private fun setUpOptions(phoneNumber: String, activity: Activity): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(FitWithUsApplication.getFirebaseAuthInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            listener?.onPhoneVerificationDone(p0)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            p0.message?.debugLogs(javaClass.simpleName)
            listener?.onPhoneVerificationCancelled()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            listener?.phoneOtpSharedSuccessFully(p0)
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
            listener?.otpNotSharedSuccessFully()
        }

    }

}