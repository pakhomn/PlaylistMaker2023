package com.example.playlistmaker2023

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity


internal class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener  {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val shareButton = findViewById<ImageView>(R.id.button_share)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton = findViewById<ImageView>(R.id.button_support)
        supportButton.setOnClickListener {
            writeToSupport()
        }

        val userAgreementButton = findViewById<ImageView>(R.id.button_user_agreement)
        userAgreementButton.setOnClickListener {
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

        with(Intent(Intent.ACTION_SENDTO)) {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, emailAddress)
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(this)
        }
    }

    private fun moveToOffertInBrowser() {

        val userAgreementLink = getString(R.string.user_agreement_link)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementLink))

        startActivity(browserIntent)
    }

    private fun syncNightModeWithSystem() {
        val nightModeSwitch = findViewById<Switch>(R.id.night_mode_switch)
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()

        when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> nightModeSwitch.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> nightModeSwitch.isChecked = false
        }
    }

}
