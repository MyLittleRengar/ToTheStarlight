package com.project.tothestarlight

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vimalcvs.switchdn.DayNightSwitch
import com.vimalcvs.switchdn.DayNightSwitchAnimListener



class SettingActivity : AppCompatActivity() {

    private lateinit var settingBackIv: ImageView
    private lateinit var settingLocationTv: TextView
    private lateinit var nightSw: DayNightSwitch
    private lateinit var preference: SharedPreferences

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

        preference = getSharedPreferences("location", 0)

        val selectedLocation = preference.getString("location", "").toString()
        if(selectedLocation.isBlank()) {
            settingLocationTv.text = "서울"
        }
        else {
            settingLocationTv.text = selectedLocation
        }
        settingLocationTv.setOnClickListener {
            val dlg = CustomLocationDialogAdapter(this@SettingActivity)
            dlg.setOnAcceptClickedListener { location ->
                settingLocationTv.text = location
                val editor = preference.edit()
                editor.putString("location", location)
                editor.apply()
                Toast.makeText(this, "다음 정보부터 적용된 지역으로 표시됩니다.", Toast.LENGTH_SHORT).show()
            }
            dlg.show()
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
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SettingActivity, MainActivity::class.java))
        finish()
    }
}