package com.example.playlistmaker2023

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
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

const val HISTORY_TRACK = "history_track"
const val HISTORY_TRACK_KEY = "history_track_key"
class SearchActivity : AppCompatActivity(), TrackAdapter.OnTrackClickListener {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private var searchText: String = ""

    private val adapterList = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter

    private val iTunesBaseURL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesAPIService = retrofit.create(ITunesAPI::class.java)

    private lateinit var errorNothingWasFound: LinearLayout
    private lateinit var recyclerViewTrack: RecyclerView
    private lateinit var errorAnother: LinearLayout
    private lateinit var newConnectionButton: Button

    private lateinit var historyLayout: LinearLayout
    private lateinit var recyclerViewTrackHistory: RecyclerView
    private lateinit var cleanHistory: Button
    private val adapterListHistory = ArrayList<Track>()
    private lateinit var adapterHistory: TrackAdapter

    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initializeViews()
        setupListeners()
        restoreSavedState(savedInstanceState)

        searchHistory = SearchHistory(getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE))
        adapter = TrackAdapter(adapterList, searchHistory, false)
        adapter.setOnTrackClickListener(this)
        adapterHistory = TrackAdapter(adapterListHistory, searchHistory, false)
        adapterHistory.setOnTrackClickListenerHistory(this)


        recyclerViewTrackHistory = findViewById(R.id.track_history)
        recyclerViewTrackHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewTrackHistory.adapter = adapterHistory

        errorNothingWasFound = findViewById(R.id.not_found)
        recyclerViewTrack = findViewById(R.id.track)
        errorAnother = findViewById(R.id.smth_wrong)
        historyLayout = findViewById(R.id.history)
        cleanHistory = findViewById(R.id.clean_history)

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

        cleanHistory.setOnClickListener {
            searchHistory.clearSearchHistory()
            adapterListHistory.clear()
            adapterHistory.notifyDataSetChanged()

            hideSearchHistory()
        }
        loadAndDisplaySearchHistory()
    }

    override fun onTrackClick(track: Track) {
        Log.d("SearchActivity", "onTrackClick отработал: ${track.trackName}")

        if (!searchHistory.hasSearch(track)) {
            searchHistory.saveSearch(track)
            loadAndDisplaySearchHistory()
        }

        val pleerIntent = Intent(this, PleerActivity::class.java)
        pleerIntent.putExtra(Constants.TRACK, track)
        startActivity(pleerIntent)
    }

    fun onTrackClickHistory(track: Track) {
        // Обработка клика на трек из истории поиска
        val pleerIntent = Intent(this, PleerActivity::class.java)
        pleerIntent.putExtra(Constants.TRACK, track)
        startActivity(pleerIntent)
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

            if (hasFocus && searchEditText.text.isEmpty() && searchHistory.hasTracksInHistory()) {
                showSearchHistory()
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
            if (searchHistory.hasTracksInHistory()) {
                showSearchHistory()
            }
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
                                hideSearchHistory()

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
            historyLayout.visibility = View.GONE
            cleanHistory.visibility = View.GONE
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
            historyLayout.visibility = View.GONE
            cleanHistory.visibility = View.GONE
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
    private fun loadAndDisplaySearchHistory() {
        val historyList = searchHistory.getSearchHistory()
        adapterListHistory.clear()
        adapterListHistory.addAll(historyList)
        adapterHistory.notifyDataSetChanged()
    }
    private fun showSearchHistory() {
        historyLayout.visibility = View.VISIBLE
        cleanHistory.visibility = View.VISIBLE
    }
    private fun hideSearchHistory() {
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        historyLayout.startAnimation(fadeOut)
        cleanHistory.startAnimation(fadeOut)

        historyLayout.visibility = View.GONE
        cleanHistory.visibility = View.GONE
    }
    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
        private const val SEARCH_HISTORY_KEY = "SearchHistory"
    }
}