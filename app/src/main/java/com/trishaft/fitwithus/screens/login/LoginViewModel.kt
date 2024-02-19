package com.trishaft.fitwithus.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel


/*AndroidViewModel provide us the scope of the calling parent by default
* so we don't need to pass the context from one class to other.
* */

class LoginViewModel(application: Application) : AndroidViewModel(application){
}