package com.trishaft.fitwithus.utilities.safe_args_modals

import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.parcelize.Parcelize

@Parcelize
data class OTPArgs (
    val otpToken:String,
    val resendToken:PhoneAuthProvider.ForceResendingToken
):Parcelable