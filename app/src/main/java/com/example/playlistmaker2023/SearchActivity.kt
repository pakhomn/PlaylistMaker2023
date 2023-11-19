package com.example.playlistmaker2023

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private var searchText: String = ""

    private val adapterList = ArrayList<Track>()
    private val adapter = TrackAdapter(adapterList)

    private val iTunesBaseURL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesAPIService = retrofit.create(ITunesAPI::class.java)

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }

    private lateinit var errorNothingWasFound: LinearLayout
    private lateinit var recyclerViewTrack: RecyclerView
    private lateinit var errorAnother: LinearLayout
    private lateinit var newConnectionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initializeViews()
        setupListeners()
        restoreSavedState(savedInstanceState)

        errorNothingWasFound = findViewById(R.id.not_found)
        recyclerViewTrack = findViewById(R.id.track)
        errorAnother = findViewById(R.id.smth_wrong)

        showTrackList()

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchFromAPI(searchEditText.text.toString())
                true
            }
            false
        }

        newConnectionButton = findViewById(R.id.new_connection_button)
        newConnectionButton.setOnClickListener {
            if (errorAnother.visibility == View.VISIBLE) {
                refreshLastFailedSearch()
            }
        }
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

            adapterList.clear()
            adapter.notifyDataSetChanged()
            errorNothingWasFound.visibility = View.GONE
            errorAnother.visibility = View.GONE
            recyclerViewTrack.visibility = View.VISIBLE
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

    private fun showTrackList() {
        val recyclerView: RecyclerView = findViewById(R.id.track)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun searchFromAPI(query: String) {
        if (query.isNotEmpty()) {
            iTunesAPIService.search(query).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if (response.isSuccessful) {
                        val trackResponse = response.body()
                        if (trackResponse != null) {
                            if (trackResponse.results.isNotEmpty()) {
                                adapter.updateList(trackResponse.results)
                                errorNothingWasFound.visibility = View.GONE
                                errorAnother.visibility = View.GONE
                                recyclerViewTrack.visibility = View.VISIBLE
                            } else {
                                showNotFoundText(getString(R.string.not_found), "")
                            }
                        } else {
                            showAnotherErrorText(getString(R.string.something_went_wrong), response.code().toString())
                        }
                    } else {
                        showAnotherErrorText(getString(R.string.something_went_wrong), response.code().toString())
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showAnotherErrorText(getString(R.string.something_went_wrong), t.message.toString())
                }
            })
        }
    }


    private fun showNotFoundText(text: String, additionalMessage: String){
        if (text.isNotEmpty()) {
            recyclerViewTrack.visibility= View.GONE
            errorNothingWasFound.visibility= View.VISIBLE
            adapterList.clear()
            adapter.notifyDataSetChanged()
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            errorNothingWasFound.visibility= View.GONE
        }
    }

    private fun showAnotherErrorText(text: String, additionalMessage: String){
        if (text.isNotEmpty()) {
            recyclerViewTrack.visibility= View.GONE
            errorAnother.visibility= View.VISIBLE
            adapterList.clear()
            adapter.notifyDataSetChanged()
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            errorAnother.visibility= View.GONE
        }
    }

    private fun refreshLastFailedSearch() {
        if (errorAnother.visibility == View.VISIBLE) {
            val lastQuery = searchEditText.text.toString()
            searchFromAPI(lastQuery)
        }
    }
}
