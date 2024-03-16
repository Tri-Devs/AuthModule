package com.trishaft.fitwithus.utilities

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trishaft.fitwithus.screens.remember_me.RememberMeModal

class PrefManager(context: Context) {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private var instance: PrefManager? = null
        fun getInstance(context: Context) = instance ?: PrefManager(context)
        const val PROFILE_LIST = "PROFILE_LIST"
    }

    init {
        sharedPreferences = context.getSharedPreferences(
            Constants.PERF_NAME, Context.MODE_PRIVATE
        )
    }


    fun saveProfile(data: RememberMeModal) {
        sharedPreferences.edit().apply {
            val profileList = getProfile()
            profileList.add(0, data)
            putString(PROFILE_LIST, Gson().toJson(profileList))
            apply()

        }
    }

    fun getProfile(): ArrayList<RememberMeModal> {
        if (sharedPreferences.getString(PROFILE_LIST, "")
                ?.isEmpty() == true
        )
            return ArrayList()
        return Gson().fromJson(
            sharedPreferences.getString(PROFILE_LIST, ""),
            object : TypeToken<ArrayList<RememberMeModal>>() {}.type
        )
    }
}