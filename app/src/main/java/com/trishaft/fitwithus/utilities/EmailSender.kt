package com.trishaft.fitwithus.utilities


import android.content.Context
import com.trishaft.fitwithus.R
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random


data class EmailData(
    val receiverName: String,
    val senderName: String?,
    val password: String?
)

class EmailSender {
    companion object {
        private var instance: EmailSender? = null

        fun getInstance(): EmailSender {
            return instance ?: EmailSender().also { instance = it }
        }
    }

    fun sendEmail(context : Context, emailData: EmailData, emailStatusCallback : (Boolean, String? , String? ) -> Unit) {
        val username = emailData.senderName
        val recipientEmail = emailData.receiverName

        val session = Session.getInstance(setProperties(), object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(emailData.senderName, emailData.password)
            }
        })

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(username))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
            message.subject = context.getString(R.string.otp_for_password_change)
            val otp = generateOtp()
            message.setText(context.getString(
                    R.string.hi_your_otp_is_for_this_session,
                    emailData.receiverName,
                    otp
                ))
            Transport.send(message)
            emailStatusCallback(true, null , otp)
        } catch (e: Exception) {
            e.printStackTrace()
            emailStatusCallback(false ,e.message , null )
        }
    }

    private fun generateOtp(): String {
        val randomNumber = Random.nextInt(Constants.OTP_STARTING_RANGE, Constants.OTP_ENDING_RANGE) // Generates a random 6-digit number
        return randomNumber.toString()
    }

    private fun setProperties(): Properties {
        val props = Properties()
        props["mail.smtp.auth"] =
            "true" // authorization is required to send the email from an account
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] =
            "smtp.gmail.com"  // use the google smtp server . this is the free server from google to share emails
        props["mail.smtp.port"] = "587"  // free port by google to share email.
        return props
    }

}