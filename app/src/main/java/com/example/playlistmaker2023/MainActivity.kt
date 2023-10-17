package com.example.playlistmaker2023

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.big_button_search)
        val buttonMedia = findViewById<Button>(R.id.big_button_media)
        val buttonSettings = findViewById<Button>(R.id.big_button_settings)

        buttonSearch.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(displayIntent)


                /* убираем код тоста
                                Toast.makeText(this@MainActivity, "Вы кликнули на Поиск", Toast.LENGTH_SHORT).show()
                                 */
            }
        })

        buttonMedia.setOnClickListener{

            val displayIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(displayIntent)

            /* убираем код тоста
                        Toast.makeText(this@MainActivity, "Вы кликнули на Медиатека", Toast.LENGTH_SHORT).show()
                         */
        }

        buttonSettings.setOnClickListener{
            val displayIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(displayIntent)

            /* предыдущий код
                        Toast.makeText(this@MainActivity, "Вы кликнули на Настройки", Toast.LENGTH_SHORT).show()
                         */
        }
    }
}