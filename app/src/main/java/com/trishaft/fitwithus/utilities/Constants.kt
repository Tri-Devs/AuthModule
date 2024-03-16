package com.trishaft.fitwithus.utilities

object Constants {

    const val PERF_NAME: String = "SHARED_PREF"

    /*
        * Request codes
        * */
    const val KEY_GOOGLE_REQUEST_CODE: Int = 1001

    /*
    * Reqex
    * */
    const val PASSWORD_REGEX: String =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"

    const val ENABLED_ALPHA = 1f
    const val DISABLED_ALPHA  = 0.5f
    const val ENABLE_STATE_TIME = 2000L
}