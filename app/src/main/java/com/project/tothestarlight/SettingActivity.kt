package com.project.tothestarlight

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.tothestarlight.dialog.DialogDismissListener
import com.project.tothestarlight.dialog.LocationDialogFragment
import com.project.tothestarlight.dialog.WeatherLocationDialogFragment
import com.vimalcvs.switchdn.DayNightSwitch
import com.vimalcvs.switchdn.DayNightSwitchAnimListener



class SettingActivity : AppCompatActivity(), DialogDismissListener {

    private lateinit var settingBackIv: ImageView
    private lateinit var settingLocationTv: TextView
    private lateinit var settingWeatherLocationTv: TextView
    private lateinit var nightSw: DayNightSwitch
    private lateinit var startFirstDaySp: Spinner
    private lateinit var preference: SharedPreferences
    private lateinit var firstDatPf: SharedPreferences

    private var selectedDay = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingBackIv = findViewById(R.id.settingBackIv)
        settingLocationTv = findViewById(R.id.settingLocationTv)
        settingWeatherLocationTv = findViewById(R.id.settingWeatherLocationTv)
        nightSw = findViewById(R.id.nightSw)
        startFirstDaySp = findViewById(R.id.startFirstDaySp)

        preference = getSharedPreferences("location", 0)

        val selectedLocation = preference.getString("location", "").toString()
        val selectedWeatherLocation = preference.getString("weatherLocation", "").toString()
        if(selectedLocation.isBlank()) {
            settingLocationTv.text = "서울"
        }
        else {
            settingLocationTv.text = selectedLocation
        }

        if(selectedWeatherLocation.isBlank()) {
            settingWeatherLocationTv.text = "서울"
        }
        else {
            settingWeatherLocationTv.text = selectedWeatherLocation
        }

        settingLocationTv.setOnClickListener {
            val dialog = LocationDialogFragment()
            dialog.listener = this
            dialog.show(supportFragmentManager, "LocationDialogFragment")
        }

        settingWeatherLocationTv.setOnClickListener {
            val dialog = WeatherLocationDialogFragment()
            dialog.listener = this
            dialog.show(supportFragmentManager, "WeatherLocationDialogFragment")
        }

        nightSw.setListener { isNight ->
            if(isNight) {
                Toast.makeText(this@SettingActivity, "밤", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@SettingActivity, "낮", Toast.LENGTH_SHORT).show()
            }
        }

        nightSw.setAnimListener(object: DayNightSwitchAnimListener{
            override fun onAnimStart() {}

            override fun onAnimEnd() {}

            override fun onAnimValueChanged(value: Float) {}
        })

        settingBackIv.setOnClickListener {
            startActivity(Intent(this@SettingActivity, MainActivity::class.java))
            finish()
        }

        val items = resources.getStringArray(R.array.firstDay)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        startFirstDaySp.adapter = spinnerAdapter
        startFirstDaySp.onItemSelectedListener = object: AdapterView.OnItemSelectedListener  {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                firstDatPf = getSharedPreferences("firstDay", 0)
                when(position) {
                    0 -> {
                        selectedDay = "MONDAY"
                    }
                    1-> {
                        selectedDay = "TUESDAY"
                    }
                    2-> {
                        selectedDay = "WEDNESDAY"
                    }
                    3-> {
                        selectedDay = "THURSDAY"
                    }
                    4-> {
                        selectedDay = "FRIDAY"
                    }
                    5-> {
                        selectedDay = "SATURDAY"
                    }
                    else -> {
                        selectedDay = "SUNDAY"
                    }
                }
                firstDayPreferences()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun firstDayPreferences() {
        val editor = firstDatPf.edit()
        editor.putString("firstDay", selectedDay)
        editor.apply()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SettingActivity, MainActivity::class.java))
        finish()
    }

    override fun onDialogDismissed() {
        val selectedLocation = preference.getString("location", "").toString()
        val selectedWeatherLocation = preference.getString("weatherLocation", "").toString()
        settingLocationTv.text = selectedLocation
        settingWeatherLocationTv.text = selectedWeatherLocation
    }
}