package com.trishaft.fitwithus.utilities

import android.app.Application
import android.content.Context

class FitWithUsApplication : Application() {

    /*
    * if require the context at that place where it is not avialble
    * */
    fun getInstance(): Context {
        return applicationContext
    }

}