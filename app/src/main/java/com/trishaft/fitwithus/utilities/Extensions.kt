package com.trishaft.fitwithus.utilities

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun String.debugLogs(tag: String) {
    Log.e(tag, "debugLogs: $this")
}

fun String.debugToast(context: Context, isLong: Boolean = false) {
    if (isLong)
        Toast.makeText(context, this, Toast.LENGTH_LONG).show()
    else {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}


/*
* @param --> String that need to be matched with the email address
* @return --> return true if the string matches the Required format else return format.
* */
fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


/*
* @param --> String that need to be matched with the Password
* @return --> return true if the string matches the Required format else return format.
* */
fun String.isValidPassword(): Boolean {
    return this.matches(Constants.PASSWORD_REGEX.toRegex())
}


/*
* @param --> takes the string that needs to be validate to number.
* @return --> return true if mobile number is valid else return false.
* */
fun String.isValidMobileNumber(): Boolean {
    return this.trim().length == 10
}


fun View.closeKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Fragment.navigate(action: Int) {
    findNavController().navigate(action)
}