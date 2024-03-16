package com.trishaft.fitwithus.firebase

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.trishaft.fitwithus.utilities.Constants
import com.trishaft.fitwithus.utilities.debugLogs

class RemoteConfigManager {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    /*These are the set of values if somehow the remote config fails to deliver the data then we will use this value*/
    private val defaultValues: Map<String, Any> = mapOf(
        Constants.DEFAULT_EMAIL_HEADING to Constants.DEFAULT_SENDER_EMAIL,
        Constants.DEFAULT_EMAIL_PASSWORD_HEADING to Constants.DEFAULT_SENDER_EMAIL_PASSWORD
    )


    companion object {
        private var instance: RemoteConfigManager? = null

        fun getRemoteConfigInstance(): RemoteConfigManager {
            return instance ?: synchronized(this) {
                RemoteConfigManager().also { instance = it }
            }
        }

    }


/*
* This config setting is set to 0 so that we can get the updated values from the config as soon as possible.
* */
    private fun askForRemoteConfig() {
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(defaultValues)
    }


    /*
    * We are using this because we are getting the value from the backend once to share the email.
    * */

    private fun askForTheKeys(activity: Activity, context: Context) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                   task.result.toString().debugLogs(javaClass.simpleName)
                } else {
                    Toast.makeText(context, task.isSuccessful.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }


    /*
    * This method return the email and password for sharing the email. I am calling this method
    * as many time as user will open the bottom sheet because there may be chances that the values
    * may be change from the backend.
    * */

    fun askForRemoteConfigKeys(activity: Activity, context: Context) : Pair<String , String> {
        askForRemoteConfig()
        askForTheKeys(activity, context)
        val email = remoteConfig.getString(Constants.DEFAULT_EMAIL_HEADING)
        val password = remoteConfig.getString(Constants.DEFAULT_EMAIL_PASSWORD_HEADING)
        return Pair(email , password)
    }

}