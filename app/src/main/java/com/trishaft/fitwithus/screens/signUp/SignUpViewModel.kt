package com.trishaft.fitwithus.screens.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trishaft.fitwithus.AuthRepo
import com.trishaft.fitwithus.communicators.AuthenticationCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel(){
    private lateinit var authRepo: AuthRepo

    init {
        initRepo()
    }

    private fun initRepo() {
        authRepo = AuthRepo.getInstance()
    }

    fun doEmailSignUp(email:String,password:String,listener:AuthenticationCallback){
        viewModelScope.launch(Dispatchers.IO){
            authRepo.doEmailSignUp(email,password,listener)
        }
    }

}