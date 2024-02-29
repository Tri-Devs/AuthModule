package com.trishaft.fitwithus.utilities

import android.text.Editable
import android.text.TextWatcher

 enum class ValidationManager : TextWatcher {


     Email{
         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
             TODO("Not yet implemented")
         }

         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
             TODO("Not yet implemented")
         }

         override fun afterTextChanged(s: Editable?) {

         }
     }

}