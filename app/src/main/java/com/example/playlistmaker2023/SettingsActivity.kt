package com.example.playlistmaker2023

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity


internal class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /*
        val buttonSettingsBack = findViewById<LinearLayout>(R.id.big_button_settings_back)

        buttonSettingsBack.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

         */

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener  {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val buttonShare = findViewById<ImageView>(R.id.button_share)
        buttonShare.setOnClickListener {
            shareApp()
        }

        val buttonSupport = findViewById<ImageView>(R.id.button_support)
        buttonSupport.setOnClickListener {
            writeToSupport()
        }

        val buttonUserAgreement = findViewById<ImageView>(R.id.button_user_agreement)
        buttonUserAgreement.setOnClickListener {
            moveToOffertInBrowser()
        }

    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        val appLink = getString(R.string.link_for_sharing)
        val share = getString(R.string.share)
        sendIntent.putExtra(Intent.EXTRA_TEXT, appLink)
        sendIntent.type = "text/plain"

        val shareIntent = Intent.createChooser(sendIntent, share)
        startActivity(shareIntent)
    }

    private fun writeToSupport() {
        val emailAddress = arrayOf(getString(R.string.support_email))
        val subject = getString(R.string.support_email_subject)
        val message = getString(R.string.support_email_text)

        val emailIntent = Intent(Intent.ACTION_SENDTO)

        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        emailIntent.putExtra(Intent.EXTRA_TEXT, message)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

        startActivity(emailIntent)
    }

    private fun moveToOffertInBrowser() {

        val userAgreementLink = getString(R.string.user_agreement_link)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementLink))

        startActivity(browserIntent)
    }
}
