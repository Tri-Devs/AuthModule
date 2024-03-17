package com.trishaft.fitwithus.firebase

import android.content.Context
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.communicators.IGlobalCallbacks
import com.trishaft.fitwithus.utilities.FitWithUsApplication
import com.trishaft.fitwithus.utilities.debugLogs
import com.trishaft.fitwithus.utilities.debugToast

class EmailAuthenticationManager {

    /**
     *  This companion object will help in creating the instance of this class only one time.
     */
    companion object {
        const val MAIN_LOGGER = "EmailAuthentication Manager"
        private var authInstance: EmailAuthenticationManager? = null
        fun getInstance() = authInstance ?: EmailAuthenticationManager().also { authInstance = it }
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
                listener.onFailedAuthorization(
                    it.message ?: "failed to fetch the exception message"
                )
                it.printStackTrace()
            }
            .addOnSuccessListener {
                listener.onSuccessfulAuthorization(it.user)
            }
            .addOnCanceledListener {
                listener.onAuthorizationCanceled()
            }
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    listener.onFailedAuthorization(
                        task.exception?.message ?: "failed to fetch the message"
                    )
                    task.exception?.printStackTrace()
                    return@addOnCompleteListener
                }
                listener.onAuthorizationComplete(task)
            }

    }


    fun sendThePasswordResetMail(email: String, listener : IGlobalCallbacks ) {
        FitWithUsApplication.getFirebaseAuthInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                listener.successCallback()
                if (task.isSuccessful) {
                    "Mail has been shared Successfully  $email".debugLogs("SharePassword")
                } else {
                    "Please try again after some time  $email".debugLogs("SharePassword")
                }
            }
            .addOnCanceledListener {
                listener.failureCallback()
                "Mail Sharing Process has been Stopped  $email".debugLogs("SharePassword") }
            .addOnFailureListener {
                listener.failureCallback()
                "Please try again after some time  $email".debugLogs("SharePassword") }
            .addOnSuccessListener {
                listener.successCallback()
                "Please try again after some time  $email".debugLogs("SharePassword") }
    }

/**
 *  function to sign In the user using there email and password
 */
fun doSignIn(
    email: String,
    password: String,
    listener: AuthenticationCallback
) {
    FitWithUsApplication.getFirebaseAuthInstance()
        .signInWithEmailAndPassword(email, password)
        .addOnFailureListener {
            listener.onFailedAuthorization(it.message ?: "failed to fetch the message")
            it.printStackTrace()
        }
        .addOnSuccessListener {
            listener.onSuccessfulAuthorization(it.user)
        }
        .addOnCanceledListener { listener.onAuthorizationCanceled() }
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                listener.onFailedAuthorization(
                    task.exception?.message ?: "failed to fetch the message"
                )
                task.exception?.printStackTrace()
                return@addOnCompleteListener
            }
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