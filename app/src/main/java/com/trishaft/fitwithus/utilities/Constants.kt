package com.trishaft.fitwithus.utilities

object Constants {

    /*
    * Request codes
    * */
    const val KEY_GOOGLE_REQUEST_CODE: Int = 1001

    /*
    * Reqex
    * */
    const val PASSWORD_REGEX: String = "^(?=.*[a-z])(?=.*[A-Z])[A-Za-z\\d@\$!%*?&]{8,}$"

    const val ENABLED_ALPHA = 1f
    const val DISABLED_ALPHA  = 0.5f
    const val ENABLE_STATE_TIME = 2000L
    const val DEFAULT_SENDER_EMAIL = "trishaft1@gmail.com"
    const val DEFAULT_SENDER_EMAIL_PASSWORD = "anau ashi dsoi wnnx"
    const val DEFAULT_EMAIL_PASSWORD_HEADING ="SenderEmailPassword"
    const val DEFAULT_EMAIL_HEADING = "senderEmail"
    const val OTP_STARTING_RANGE = 100000
    const val OTP_ENDING_RANGE = 999999
}