package com.trishaft.fitwithus.communicators

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

interface IPhoneAuthenticatorCallbacks {
    fun onPhoneVerificationDone(credentials: PhoneAuthCredential)
    fun onPhoneVerificationCancelled(po: FirebaseException)
    fun phoneOtpSharedSuccessFully(codeShared: String, resendToken:PhoneAuthProvider.ForceResendingToken)
    fun otpNotSharedSuccessFully()
}