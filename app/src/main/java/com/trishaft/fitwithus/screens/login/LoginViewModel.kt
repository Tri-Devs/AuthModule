package com.trishaft.fitwithus.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.trishaft.fitwithus.AuthRepo
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import com.trishaft.fitwithus.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*AndroidViewModel provide us the scope of the calling parent by default
* so we don't need to pass the context from one class to other.
* */

class LoginViewModel(application: Application) : AndroidViewModel(application){

    private lateinit var authRepo: AuthRepo

    init {
        initRepo()
    }

    private fun initRepo() {
        authRepo = AuthRepo.getInstance()
    }

    fun doEmailSignIn(email:String,password:String,listener: AuthenticationCallback){
        viewModelScope.launch(Dispatchers.IO){
            authRepo.doEmailSignIn(email,password,listener)
        }
    }

}