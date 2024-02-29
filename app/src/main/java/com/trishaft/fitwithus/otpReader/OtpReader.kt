package com.trishaft.fitwithus.otpReader

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import com.trishaft.fitwithus.R
import kotlin.math.log

class OtpReader : LinearLayout {



    private var pin1: AppCompatEditText? = null
    private var pin2: AppCompatEditText? = null
    private var pin3: AppCompatEditText? = null
    private var pin4: AppCompatEditText? = null
    private var pin5: AppCompatEditText? = null
    private var pin6: AppCompatEditText? = null

    private var listener: IOtpReader? = null
    private var userEnteredOtp: MutableList<String> =
        mutableListOf<String>("-1", "-1", "-1", "-1", "-1", "-1")

    constructor(context: Context) : super(context) {
        registerTheViews()
    }


    /*
    * This block is called Most of the times.
    * WhenEver we pass the new  properties to this
    * */
    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        registerTheViews()
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        registerTheViews()
    }


    fun registerCallbacks(callback: IOtpReader) {
        listener = callback
    }

    private fun registerTheViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_otp_reader, this, true);

        pin1 = view.findViewById(R.id.pin1)
        pin2 = view.findViewById(R.id.pin2)
        pin3 = view.findViewById(R.id.pin3)
        pin4 = view.findViewById(R.id.pin4)
        pin5 = view.findViewById(R.id.pin5)
        pin6 = view.findViewById(R.id.pin6)






        pin1?.doAfterTextChanged { pin -> updateString(pin1, pin2, pin, 0) }
        pin2?.doAfterTextChanged { pin -> updateString(pin1, pin3, pin, 1) }
        pin3?.doAfterTextChanged { pin -> updateString(pin2, pin4, pin, 2) }
        pin4?.doAfterTextChanged { pin -> updateString(pin3, pin5, pin, 3) }
        pin5?.doAfterTextChanged { pin -> updateString(pin4, pin6, pin, 4) }
        pin6?.doAfterTextChanged { pin -> updateString(pin5, pin6, pin, 5, true) }
    }

    private fun updateString(
        previousView: AppCompatEditText?,
        nextView: AppCompatEditText?,
        editable: Editable?,
        index: Int,
        isLastBit: Boolean = false
    ) {
        if (editable.toString().trim().isEmpty()) {
            previousView?.requestFocus()
        } else {
            nextView?.requestFocus()
        }
        userEnteredOtp.add(index, editable.toString())
        val convertedString = convertListToString(userEnteredOtp)
        if (isLastBit) {
            if (convertedString.second) listener?.userEnteredOtp(convertedString.first)
            else listener?.invalidOtp("user has not entered the whole Otp ", convertedString.first)
        } else {
            listener?.userEnteredOtp(convertedString.first)
        }

    }

    private fun convertListToString(list: MutableList<String>): Pair<String, Boolean> {
        var isStringComplete = true
        var resultantString = ""
        list.forEach { pin ->
            if (pin == "-1") {
                isStringComplete = false
            }
            resultantString += pin
        }
        return Pair(resultantString, isStringComplete)
    }


}

