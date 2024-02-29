package com.trishaft.fitwithus.otpReader

interface IOtpReader {
    fun userEnteredOtp(otp : String)
    fun invalidOtp(errorMessage : String , otp : String)
}