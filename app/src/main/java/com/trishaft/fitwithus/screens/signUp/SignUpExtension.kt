package com.trishaft.fitwithus.screens.signUp

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.utilities.Constants
import com.trishaft.fitwithus.utilities.isValidEmail


/**
 *  function which is used to handle the multiple click so that avoid the cause for the multiple click.
 */
fun View.performSingleClick(handler: Handler, operation:()->Unit){
    toggleState(false, false)
    operation()
    handler.postDelayed({
        toggleState(true)
    },Constants.ENABLE_STATE_TIME)
}

/**
 *  function to change the state of the view from the enabled and disabled state
 */
fun View.toggleState(state:Boolean, isAlphaChange:Boolean = true){
    if(!state){
        isEnabled = false
        if(isAlphaChange) alpha = Constants.DISABLED_ALPHA
        return
    }

    isEnabled = true
    if(isAlphaChange) alpha = Constants.ENABLED_ALPHA
}

/**
 *  function which is used to enable and disable the sign up button on the text changed from the email and password
 *  acc to the user input.
 */
fun MaterialButton.validate(context: Context, email:TextInputEditText, password:TextInputEditText){
    Log.d("ValidationLogger", email.validateEmail(context).toString())
    Log.d("ValidationLogger", password.validatePassword(context).toString())

    if(!email.validateEmail(context) || !password.validatePassword(context)){
        toggleState(false)
        return
    }

    toggleState(true)
}

/**
 *  function which is used to perform the validation for the email field.
 */
inline fun TextInputEditText.validateEmail(context: Context, emailErrorView:TextInputLayout? = null, signUp:MaterialButton?= null, success:(()->Unit) = { }):Boolean{
    if(text?.trim().toString().isEmpty()){
        emailErrorView?.error = context.getString(R.string.empty_email_msg)
        signUp?.toggleState(false)
        return false
    }

    if(!text?.trim().toString().isValidEmail()){
        emailErrorView?.error = context.getString(R.string.invalid_email_address)
        signUp?.toggleState(false)
        return false
    }

    emailErrorView?.error = null
    success()
    return true
}

/**
 *  function for the validation for the password field.
 *  Here we have to make the error icon to null otherwise the password toggle icon will not be visible.
 */
inline fun TextInputEditText.validatePassword(context: Context,passwordErrorView:TextInputLayout?= null, signUp:MaterialButton?=null,success: (() -> Unit)={}):Boolean{
    if(text?.trim().toString().isEmpty()){
        passwordErrorView?.error = context.getString(R.string.empty_pass)
        passwordErrorView?.errorIconDrawable = null
        signUp?.toggleState(false)
        return false
    }

    if(text?.trim().toString().length<6){
        passwordErrorView?.error = context.getString(R.string.password_length)
        passwordErrorView?.errorIconDrawable = null
        signUp?.toggleState(false)
        return false
    }

    passwordErrorView?.error = null
    success()
    return true
}


