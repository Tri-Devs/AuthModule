package com.trishaft.fitwithus.utilities

import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.trishaft.fitwithus.R

class SnackBarManager {

    companion object {
        @Volatile
        private var instance: SnackBarManager? = null

        fun getInstance(): SnackBarManager {
            return instance ?: synchronized(this) {
                instance ?: SnackBarManager().also { instance = it }
            }
        }
    }


    fun showSnackBar( resource: View, length: Int, text: String) {
        Snackbar.make(resource, text, length).apply {
            view.backgroundTintMode = PorterDuff.Mode.DST_ATOP
            view.setBackgroundColor(ContextCompat.getColor(resource.context, R.color.yellow_orange))
        }.show()

    }

    fun permissionUpdateBars(
        resource: View,
        text: String,
        operationText: String,
        operation: () -> Unit
    ) {
        Snackbar.make(resource, text, BaseTransientBottomBar.LENGTH_INDEFINITE).apply {
            view.backgroundTintMode = PorterDuff.Mode.DST_ATOP
            view.setBackgroundColor(ContextCompat.getColor(resource.context, R.color.yellow_orange))
        }
            .setAction(operationText) { operation() }
            .show()
    }

}