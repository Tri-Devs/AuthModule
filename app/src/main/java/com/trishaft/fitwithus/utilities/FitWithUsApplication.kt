package com.trishaft.fitwithus.utilities

import android.app.Application
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import com.trishaft.fitwithus.R

class FitWithUsApplication : Application() {



    /*
    * if require the context at that place where it is not avialble
    * */
     fun getInstance(): Context {
        return applicationContext
    }
    companion object{
        private var firebaseAuth:FirebaseAuth? = null

        fun getFirebaseAuthInstance() : FirebaseAuth{
            return firebaseAuth?: Firebase.auth.also {  firebaseAuth = it }
        }

        fun noImplementationLog(context: Context){
             context.getString(R.string.later_implementation).debugLogs(javaClass::class.java.simpleName)
        }

    }






    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(getInstance())
    }

}