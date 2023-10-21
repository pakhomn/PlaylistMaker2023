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
    var searchText: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener  {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.button_clear)
        //cursor = findViewById(R.id.cursor)

        clearButton.visibility = View.GONE
        //cursor.visibility = View.VISIBLE

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
                //cursor.visibility = View.GONE
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
                // Если ничего не написали, скрываем кнопку х, иначе - показываем
                if (s.toString().isNotEmpty()) {
                    clearButton.visibility = View.VISIBLE
                    //cursor.visibility = View.GONE
                } else {
                    clearButton.visibility = View.GONE
                    //cursor.visibility = View.VISIBLE
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchText", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText", "")
    }


    private fun showKeyboard() {
        searchEditText.requestFocus()
    }

    private fun hideKeyboard() {
        searchEditText.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    /* первое плохое решение, с фиксом
    private fun isClearButtonClicked(event: android.view.MotionEvent): Boolean {
        val drawableRight = 2
        val x = event.x.toInt()
        val width = searchEditText.width
        return x >= width - searchEditText.compoundDrawables[drawableRight].bounds.width()
    }
     */
}
