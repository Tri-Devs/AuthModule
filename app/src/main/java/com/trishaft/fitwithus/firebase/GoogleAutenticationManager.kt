package com.trishaft.fitwithus.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.trishaft.fitwithus.utilities.debugLogs


class GoogleAuthenticationManager {


    companion object {
        private var instance: GoogleAuthenticationManager? = null
        fun getTheGoogleInstance(): GoogleAuthenticationManager {
            return instance ?: GoogleAuthenticationManager().also { instance = it }
        }
    }

    /*
    * check user has already login or not to maintain the user session
    *
    * @return null if user has logout the session or not login till now
    * @return account details if user has login already and session is maintained
    *
    * */
    fun checkWhetherUserHasAlreadyLoginTheAccount(context: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context);
    }

    /*
    * Show the google sign in account to the user to choose from which account from which account he wants
    * login or sign up with.
    */
    fun showTheGoogleSignInToUser(activity: Activity): Intent {
        val mGoogleSignInClient = GoogleSignIn.getClient(activity, setUpGoogleSignUpProperties());
        return mGoogleSignInClient.signInIntent
    }

    fun removeOrChangeGoogleAccount(activity: Activity) {
        val mGoogleSignInClient = GoogleSignIn.getClient(activity, setUpGoogleSignUpProperties());
        mGoogleSignInClient.signOut()
            .addOnCompleteListener {"user has been signOut successfully".debugLogs(javaClass.simpleName)}
            .addOnFailureListener {"A failure has been occurred successfully".debugLogs(javaClass.simpleName) }
            .addOnCanceledListener {"sign out request is somehow canceled successfully".debugLogs(javaClass.simpleName) }
    }

    /*
     * Setting up the properties for showing the google account.
     */
    private fun setUpGoogleSignUpProperties(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }

}