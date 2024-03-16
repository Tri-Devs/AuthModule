package com.trishaft.fitwithus.utilities

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.trishaft.fitwithus.R
import com.trishaft.fitwithus.databinding.CustomDialogEmailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
fun String.isValidEmail(onValidate:(Boolean)->Unit ={}): Boolean {
    if(!Patterns.EMAIL_ADDRESS.matcher(this).matches()){
        onValidate(false)
        return false
    }
    onValidate(true)
    return true
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
fun String.isValidMobileNumber(onValidate:(Boolean)->Unit) {
    if(this.trim().length != 10){
        onValidate(false)
        return
    }
    onValidate(true)
}





fun View.closeKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}


fun View.startFadeAnimation() {

}

inline fun Fragment.startOnBackGroundThread(crossinline googleSignInTask: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch { googleSignInTask() }
}

inline fun Fragment.startOnMainThread(crossinline launchOnMainThread: () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch { launchOnMainThread() }
}

fun Fragment.navigate(action: Int) {
    findNavController().navigate(action)
}



fun String.showDialog(context: Context) {
    val alertDialog = AlertDialog.Builder(context).apply {
        setCancelable(true)
        setMessage(this@showDialog)
        setIcon(R.drawable.logo)
    }.show()

}

fun Context.showCustomDialog(
    layoutInflater: LayoutInflater,
    emailAccount: String,
    positiveButtonName: String,
    negativeButtonName: String,
    negativeButtonBlock: () -> Unit,
    positiveButtonBlock: () -> Unit
) {
    val customDialog = Dialog(this, R.style.dialogBackground)
    val binding = CustomDialogEmailBinding.inflate(layoutInflater)
    customDialog.setContentView(binding.root)
    binding.apply {
        this@showCustomDialog.getString(R.string.do_you_want_to_go_with_this_email_id, emailAccount)
            .also { emailString.text = it }
        positiveButton.text = positiveButtonName
        negativeButton.text = negativeButtonName
        cancelButton.setOnClickListener { customDialog.dismiss() }
        positiveButton.setOnClickListener { positiveButtonBlock(); customDialog.dismiss() }
        negativeButton.setOnClickListener { negativeButtonBlock(); customDialog.dismiss() }
    }
    customDialog.show()
}

fun Window.enableDisableScreen(enableDisable:Boolean){
    if(enableDisable){
        addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        return
    }
    clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun ProgressBar.enableDisableScreen(enableDisable: Boolean){
    if(enableDisable){
        visibility = View.VISIBLE
        return
    }

    visibility = View.GONE
}

