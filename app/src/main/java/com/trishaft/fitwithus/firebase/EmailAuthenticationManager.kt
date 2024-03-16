package com.trishaft.fitwithus.firebase

import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.utilities.FitWithUsApplication
import com.trishaft.fitwithus.utilities.debugLogs

class EmailAuthenticationManager {

    /**
     *  This companion object will help in creating the instance of this class only one time.
     */
    companion object {
        const val MAIN_LOGGER = "EmailAuthentication Manager"
        private var authInstance: EmailAuthenticationManager? = null
        fun getInstance() = authInstance ?: EmailAuthenticationManager()
    }

    /**
     *  function to perform the sign Up option for the user.
     */
    suspend fun doSignUp(
        email: String,
        password: String,
        listener: AuthenticationCallback
    ) {
        FitWithUsApplication.getFirebaseAuthInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnFailureListener {
                "onFailureCallback while signUp ${it.message}".debugLogs(MAIN_LOGGER)
                listener.onFailedAuthorization(it)
                it.printStackTrace()
            }
            .addOnSuccessListener {
                "onSuccessCallback while signUp ".debugLogs(MAIN_LOGGER)
                listener.onSuccessfulAuthorization(it.user)
            }
            .addOnCanceledListener {
                "onCanceledCallback while signUp".debugLogs(MAIN_LOGGER)
                listener.onAuthorizationCanceled()
            }
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    "onCompleteListener while signup with task unsuccessful ".debugLogs(
                        MAIN_LOGGER
                    )
                    task.exception?.let { listener.onFailedAuthorization(it) }

                    task.exception?.printStackTrace()
                    return@addOnCompleteListener
                }
                "onCompleteListener while signup with task successful ".debugLogs(MAIN_LOGGER)
                listener.onAuthorizationComplete(task)
            }

    }


    /**
     *  function to sign In the user using there email and password
     */
    fun doSignIn(
        email: String,
        password: String,
        listener: AuthenticationCallback
    ) {
        email.debugLogs("logger")
        password.debugLogs("logger")

        FitWithUsApplication.getFirebaseAuthInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                "onFailureCallback while signIn ${it.message}".debugLogs(MAIN_LOGGER)
                listener.onFailedAuthorization(
                    it
                )
                it.printStackTrace()
            }
            .addOnSuccessListener {
                "onSuccessCallback while signIn ".debugLogs(MAIN_LOGGER)
                listener.onSuccessfulAuthorization(it.user)
            }
            .addOnCanceledListener {
                "onCanceledCallback while signIn".debugLogs(MAIN_LOGGER)
                listener.onAuthorizationCanceled()
            }
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    "onCompleteListener while signIn with task unsuccessful ".debugLogs(
                        MAIN_LOGGER
                    )
                    task.exception?.let {
                        listener.onFailedAuthorization(
                            it
                        )
                    }
                    task.exception?.printStackTrace()
                    return@addOnCompleteListener
                }
                "onCompleteListener while signIn with task successful ".debugLogs(MAIN_LOGGER)
                listener.onAuthorizationComplete(task)
            }


    }

    /**
     *  function to signOut the user login from the firebase.
     */
    fun doSignOut() {
        FitWithUsApplication.getFirebaseAuthInstance().signOut()
    }
}