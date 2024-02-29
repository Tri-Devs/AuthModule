package com.trishaft.fitwithus.communicators

import com.google.firebase.auth.PhoneAuthCredential

interface IPhoneAuthenticatorCallbacks {
    fun onPhoneVerificationDone(credentials: PhoneAuthCredential)
    fun onPhoneVerificationCancelled()
    fun phoneOtpSharedSuccessFully(codeShared: String)
    fun otpNotSharedSuccessFully()
}