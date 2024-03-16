package com.trishaft.fitwithus.firebase

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.communicators.IPhoneAuthenticatorCallbacks
import com.trishaft.fitwithus.utilities.FitWithUsApplication
import com.trishaft.fitwithus.utilities.debugLogs
import java.util.concurrent.TimeUnit

class PhoneAuthManager {

    private var listener: IPhoneAuthenticatorCallbacks? = null


    companion object {
        private var instance: PhoneAuthManager? = null
        fun getInstance(): PhoneAuthManager {
            return instance ?: PhoneAuthManager().also { instance = it }
        }
    }


    suspend fun setUpPhoneAuthenticator(
        phoneNumber: String,
        activity: Activity,
        parentReference: IPhoneAuthenticatorCallbacks
    ) {
        this.listener = parentReference
        PhoneAuthProvider.verifyPhoneNumber(setUpOptions(phoneNumber, activity))
    }

    suspend fun setUpPhoneAuthenticator(
        phoneNumber: String,
        activity: Activity,
        resendToken:ForceResendingToken,
        parentReference: IPhoneAuthenticatorCallbacks
    ) {
        this.listener = parentReference
        PhoneAuthProvider.verifyPhoneNumber(setUpResendOption(phoneNumber, activity, resendToken))
    }


    private fun setUpOptions(phoneNumber: String, activity: Activity): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(FitWithUsApplication.getFirebaseAuthInstance())
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
    }

    private fun setUpResendOption(phoneNumber: String, activity: Activity, resendToken: ForceResendingToken): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(FitWithUsApplication.getFirebaseAuthInstance())
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            listener?.onPhoneVerificationDone(p0)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            p0.message?.debugLogs(javaClass.simpleName)

            listener?.onPhoneVerificationCancelled(p0)
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            listener?.phoneOtpSharedSuccessFully(p0, p1)
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
            listener?.otpNotSharedSuccessFully()
        }

    }


    suspend fun verifyUserUsingOTP(code: String, userToken: String, listener: AuthenticationCallback) {
        FitWithUsApplication.getFirebaseAuthInstance().signInWithCredential(
            getCredential(code, userToken)
        )
            .addOnCompleteListener {
                listener.onAuthorizationComplete(it)
            }
            .addOnSuccessListener { listener.onSuccessfulAuthorization(it.user) }
            .addOnFailureListener {
                listener.onFailedAuthorization(it.message.toString())
                it.printStackTrace()
            }
            .addOnCanceledListener { listener.onAuthorizationCanceled() }

    }

    private fun getCredential(code: String, userToken: String): AuthCredential {
        return PhoneAuthProvider.getCredential(
            userToken,
            code
        )
    }

}