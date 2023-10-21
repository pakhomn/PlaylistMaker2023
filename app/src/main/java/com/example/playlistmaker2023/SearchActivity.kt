package com.example.playlistmaker2023

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import android.view.inputmethod.InputMethodManager

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var cursor: ImageView
    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initializeViews()
        setupListeners()
        restoreSavedState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
    }

    private fun initializeViews() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.button_clear)
        clearButton.visibility = View.GONE
    }

    private fun setupListeners() {
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не реализуем, оставляем как есть
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Не реализуем, оставляем как есть
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    clearButton.visibility = View.VISIBLE
                } else {
                    clearButton.visibility = View.GONE
                }
                searchText = s.toString()
            }
        })

        searchEditText.setText(searchText)

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
        }
    }

    private fun restoreSavedState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        }
    }

    private fun showKeyboard() {
        searchEditText.requestFocus()
    }

    private fun hideKeyboard() {
        searchEditText.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
