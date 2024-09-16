package com.project.tothestarlight

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.tothestarlight.dialog.DialogDismissListener
import com.project.tothestarlight.dialog.LocationDialogFragment
import com.vimalcvs.switchdn.DayNightSwitch
import com.vimalcvs.switchdn.DayNightSwitchAnimListener



class SettingActivity : AppCompatActivity(), DialogDismissListener {

    private lateinit var settingBackIv: ImageView
    private lateinit var settingLocationTv: TextView
    private lateinit var nightSw: DayNightSwitch
    private lateinit var startFirstDaySp: Spinner
    private lateinit var preference: SharedPreferences
    private lateinit var firstDatPf: SharedPreferences
    private lateinit var nightModePf: SharedPreferences

    private var isFirst = true
    private var savedNightMode = false

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
        nightSw = findViewById(R.id.nightSw)
        startFirstDaySp = findViewById(R.id.startFirstDaySp)

        firstDatPf = getSharedPreferences("firstDay", 0)
        preference = getSharedPreferences("location", 0)
        nightModePf = getSharedPreferences("night", 0)
        val night = nightModePf.getString("night", "").toString()

        savedNightMode = when (night) {
            "" -> { false }
            "true" -> { true }
            else -> { false }
        }
        if(savedNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        nightSw.setIsNight(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES)

        val selectedLocation = preference.getString("location", "").toString()
        if(selectedLocation.isBlank()) {
            settingLocationTv.text = "서울"
        }
        else {
            settingLocationTv.text = selectedLocation
        }

        settingLocationTv.setOnClickListener {
            val dialog = LocationDialogFragment()
            dialog.listener = this
            dialog.show(supportFragmentManager, "LocationDialogFragment")
        }

        nightSw.setIsNight(savedNightMode)

        nightSw.setAnimListener(object: DayNightSwitchAnimListener{
            override fun onAnimStart() {}
            override fun onAnimEnd() {
                if(nightModePf.getString("night", "").toBoolean()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    val editor = nightModePf.edit()
                    editor.putString("night", "false")
                    editor.apply()
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    val editor = nightModePf.edit()
                    editor.putString("night", "true")
                    editor.apply()
                }
            }

            override fun onAnimValueChanged(value: Float) {}
        })

        settingBackIv.setOnClickListener {
            startActivity(Intent(this@SettingActivity, MainActivity::class.java))
            finish()
        }

        val items = resources.getStringArray(R.array.firstDay)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        startFirstDaySp.adapter = spinnerAdapter

        val selectedDayPref = getSharedPreferences("firstDay", 0)
        val selectedFirstDay = selectedDayPref.getString("firstDay", "").toString()
        if(selectedFirstDay.isNotEmpty()) {
            val firstDayOfWeek = getCalendarWeekDay(selectedFirstDay)
            Log.e("!!!!", firstDayOfWeek.toString())
            startFirstDaySp.setSelection(firstDayOfWeek)
        }
        else {
            startFirstDaySp.setSelection(6)
        }

        startFirstDaySp.onItemSelectedListener = object: AdapterView.OnItemSelectedListener  {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(isFirst) {
                    isFirst = false
                    return
                }

                val editor = firstDatPf.edit()
                if (view != null) {
                    firstDatPf = getSharedPreferences("firstDay", 0)
                    when(position) {
                        0 -> {
                            Log.e("@@@", "MONDAY")
                            editor.putString("firstDay", "MONDAY")
                            editor.apply()
                        }
                        1 -> {
                            Log.e("@@@", "TUESDAY")
                            editor.putString("firstDay", "TUESDAY")
                            editor.apply()
                        }
                        2 -> {
                            Log.e("@@@", "WEDNESDAY")
                            editor.putString("firstDay", "WEDNESDAY")
                            editor.apply()
                        }
                        3 -> {
                            Log.e("@@@", "THURSDAY")
                            editor.putString("firstDay", "THURSDAY")
                            editor.apply()
                        }
                        4 -> {
                            Log.e("@@@", "FRIDAY")
                            editor.putString("firstDay", "FRIDAY")
                            editor.apply()
                        }
                        5 -> {
                            Log.e("@@@", "SATURDAY")
                            editor.putString("firstDay", "SATURDAY")
                            editor.apply()
                        }
                        else -> {
                            Log.e("@@@", "SUNDAY")
                            editor.putString("firstDay", "SUNDAY")
                            editor.apply()
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getCalendarWeekDay(dayName: String): Int {
        return when (dayName) {
            "MONDAY" -> 0
            "TUESDAY" -> 1
            "WEDNESDAY" -> 2
            "THURSDAY" -> 3
            "FRIDAY" -> 4
            "SATURDAY" -> 5
            "SUNDAY" -> 6
            else -> 7
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SettingActivity, MainActivity::class.java))
        finish()
    }

    override fun onDialogDismissed() {
        val selectedLocation = preference.getString("location", "").toString()
        settingLocationTv.text = selectedLocation
    }

    override fun onResume() {
        super.onResume()
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        nightSw.setIsNight(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES)
    }

}