package com.trishaft.fitwithus.utilities

import android.content.Context
import android.util.Log
import android.widget.Toast

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