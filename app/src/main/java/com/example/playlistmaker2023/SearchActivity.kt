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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener  {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }


        /*
        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
        }


        searchEditText.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (isClearButtonClicked(event)) {
                    searchEditText.text.clear()
                }
            }
            false
        }



         */


        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.button_clear)

        clearButton.visibility = View.GONE

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Не используется
            }

            override fun afterTextChanged(s: Editable?) {
                // Если ничего не написали, скрываем кнопку, иначе - показываем
                if (s.toString().isNotEmpty()) {
                    clearButton.visibility = View.VISIBLE
                } else {
                    clearButton.visibility = View.GONE
                }
            }
        })

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
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

    private fun isClearButtonClicked(event: android.view.MotionEvent): Boolean {
        val drawableRight = 2
        val x = event.x.toInt()
        val width = searchEditText.width
        return x >= width - searchEditText.compoundDrawables[drawableRight].bounds.width()
    }

}
