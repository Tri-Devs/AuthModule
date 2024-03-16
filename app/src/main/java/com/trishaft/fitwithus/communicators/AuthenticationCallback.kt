package com.trishaft.fitwithus.communicators

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthenticationCallback {
    fun onSuccessfulAuthorization(user:FirebaseUser?)
    fun onFailedAuthorization(error:Exception)
    fun onAuthorizationCanceled()
    fun onAuthorizationComplete(task: Task<AuthResult>)
}