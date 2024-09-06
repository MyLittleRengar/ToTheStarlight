package com.project.tothestarlight

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.project.tothestarlight.databinding.ActivityMainBinding
import com.project.tothestarlight.recycler.AstroItem
import com.project.tothestarlight.recycler.AstroRecyclerAdapter
import com.project.tothestarlight.recycler.LunItem
import com.project.tothestarlight.recycler.RiseItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    companion object {
        const val REQUEST_CODE = 101
    }

    private lateinit var custom: CalendarView
    private lateinit var settingIv: ImageView
    private lateinit var openIv: ImageView
    private lateinit var closeIv: ImageView
    private lateinit var mainDateTv: TextView
    private lateinit var moonShapeIv: ImageView
    private lateinit var moonAgeTv: TextView
    private lateinit var locationTv: TextView
    private lateinit var lunarTv: TextView
    private lateinit var sunRiseTv: TextView
    private lateinit var moonRiseTv: TextView
    private lateinit var sunSetTv: TextView
    private lateinit var moonSetTv: TextView
    private lateinit var monthlyEventTv: TextView
    private lateinit var mainAstroTitleTv: TextView
    private lateinit var monthlyEventInfoTv: TextView
    private lateinit var copyIv: ImageView
    private lateinit var astroRv: RecyclerView
    private var openType: Boolean = true

    val events = mutableListOf<EventDay>()
    private var lunItems = mutableListOf<LunItem>()

    private lateinit var astroAdapter: AstroRecyclerAdapter
    private var datas = mutableListOf<AstroItem>()
    private var astroItems = mutableListOf<AstroItem>()
    private var riseItems = mutableListOf<RiseItem>()

    private var lunAge: String? = null
    private var solDay: String? = null
    private var solMonth: String? = null
    private var solWeek: String? = null
    private var solYear: String? = null

    private var astroEvent: String? = null
    private var astroTitle: String? = null
    private var astroTime: String? = null
    private var astroDate: String? = null

    private var sunRise: String? = null
    private var sunSet: String? = null
    private var moonRise: String? = null
    private var moonSet: String? = null

    private lateinit var urlBuilder1: StringBuilder
    private lateinit var urlBuilder2: StringBuilder
    private lateinit var urlBuilder3: StringBuilder

    private lateinit var preference: SharedPreferences
    private var selectedLocation = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val locale = Locale("ko", "KR")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        custom = findViewById(R.id.custom)
        settingIv = findViewById(R.id.settingIv)
        openIv = findViewById(R.id.openIv)
        closeIv = findViewById(R.id.closeIv)
        mainDateTv = findViewById(R.id.mainDateTv)
        moonShapeIv = findViewById(R.id.moonShapeIv)
        moonAgeTv = findViewById(R.id.moonAgeTv)
        locationTv = findViewById(R.id.locationTv)
        lunarTv = findViewById(R.id.lunarTv)
        sunRiseTv = findViewById(R.id.sunRiseTv)
        moonRiseTv = findViewById(R.id.moonRiseTv)
        sunSetTv = findViewById(R.id.sunSetTv)
        moonSetTv = findViewById(R.id.moonSetTv)
        monthlyEventInfoTv = findViewById(R.id.monthlyEventInfoTv)
        mainAstroTitleTv = findViewById(R.id.mainAstroTitleTv)
        monthlyEventTv = findViewById(R.id.monthlyEventTv)
        copyIv = findViewById(R.id.copyIv)
        astroRv = findViewById(R.id.astroRv)

        preference = getSharedPreferences("location", 0)

        if(locationTv.text == "위치") {
            selectedLocation = preference.getString("location", "").toString()
            locationTv.text = selectedLocation
        }

        val dateSplit = getCurrentDate().split("-")
        val currentDate = dateSplit[0]+dateSplit[1]+dateSplit[2]

        urlBuilder1 = StringBuilder(BuildConfig.API_ASTRO)
        urlBuilder2 = StringBuilder(BuildConfig.API_MOON)
        urlBuilder3 = StringBuilder(BuildConfig.API_RISE)
        makeUrlBuilder1(dateSplit[0], dateSplit[1])
        makeUrlBuilder2(dateSplit[0], dateSplit[1])
        makeUrlBuilder3(currentDate, selectedLocation)
        xmlParsing1()
        xmlParsing2()
        xmlParsing3()

        mainDateTv.text = dateSplit[0] + "년" + dateSplit[1] + "월"

        monthlyEventTv.text = dateSplit[1]

        settingIv.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            finish()
        }

        copyIv.setOnClickListener {
            copyContent()
            Toast.makeText(this, "복사하였습니다.", Toast.LENGTH_SHORT).show()
        }

        //달력 클릭시 클릭한 날짜를 음력 변환 후 출력
        custom.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            @SuppressLint("SimpleDateFormat", "DiscouragedApi")
            override fun onClick(calendarDay: CalendarDay) {
                val clickedDay = calendarDay.calendar
                val year = clickedDay.get(Calendar.YEAR)
                val month = String.format(Locale.KOREA, "%02d", clickedDay.get(Calendar.MONTH) + 1)
                val day = clickedDay.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                val convert = convertSolarToLunar(year, month.toInt(), day.toInt())

                lunarTv.text = convert

                val combineDate = year.toString()+month+ day
                Log.e("@@@@", combineDate)
                urlBuilder3.apply {
                    clear()
                    append(BuildConfig.API_RISE)
                }
                riseItems.clear()
                makeUrlBuilder3(combineDate, selectedLocation)
                xmlParsing3()

                for (i in 0 until lunItems.size) {
                    if (lunItems[i].solDay == day) {
                        moonAgeTv.text = lunItems[i].lunAge
                        val drawableId = resources.getIdentifier(
                            "moon_shape${floor(lunItems[i].lunAge.toDouble()).toInt()}",
                            "drawable",
                            packageName
                        )
                        val drawable: Drawable? =
                            ContextCompat.getDrawable(this@MainActivity, drawableId)
                        moonShapeIv.setImageDrawable(drawable)
                    }
                }
            }
        })

        //다음 달 클릭
        custom.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChange() {
                val calendarToday = custom.currentPageDate
                val year = calendarToday.get(GregorianCalendar.YEAR).toString()
                val month = calendarToday.get(GregorianCalendar.MONTH) + 1
                val convertMonth = month.toString().padStart(2, '0')
                monthlyEventTv.text = convertMonth
                //Log.e("Test", "년: $year, 월: $convertMonth, 일: $day")
                urlBuilder1.apply {
                    clear()
                    append(BuildConfig.API_ASTRO)
                }
                astroItems.clear()
                datas.clear()
                astroAdapter.notifyDataSetChanged()
                makeUrlBuilder1(year, convertMonth)
                xmlParsing2()
                urlBuilder2.apply {
                    clear()
                    append(BuildConfig.API_MOON)
                }
                lunItems.clear()
                makeUrlBuilder2(year, convertMonth)
                xmlParsing1()
            }
        })

        //저번 달 클릭
        custom.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChange() {
                val calendarToday = custom.currentPageDate
                val year = calendarToday.get(GregorianCalendar.YEAR).toString()
                val month = calendarToday.get(GregorianCalendar.MONTH) + 1
                val convertMonth = month.toString().padStart(2, '0')
                monthlyEventTv.text = convertMonth
                //Log.e("Test", "년: $year, 월: $convertMonth, 일: $day")
                urlBuilder1.apply {
                    clear()
                    append(BuildConfig.API_ASTRO)
                }
                astroItems.clear()
                datas.clear()
                astroAdapter.notifyDataSetChanged()
                makeUrlBuilder1(year, convertMonth)
                xmlParsing2()
                urlBuilder2.apply {
                    clear()
                    append(BuildConfig.API_MOON)
                }
                lunItems.clear()
                makeUrlBuilder2(year, convertMonth)
                xmlParsing1()
            }
        })

        openIv.setOnClickListener {
            if (openType) {
                openType = false
                openIv.visibility = View.GONE
                closeIv.visibility = View.VISIBLE
                custom.visibility = View.GONE
            }
        }

        closeIv.setOnClickListener {
            if (!openType) {
                openType = true
                openIv.visibility = View.VISIBLE
                closeIv.visibility = View.GONE
                custom.visibility = View.VISIBLE
            }
        }

        mainDateTv.setOnClickListener {
            val title = "달 정보"
            val content = "오늘은 아름다운 날입니다."
            val intent = Intent(binding.root.context, MyAlarmReceiver::class.java).apply {
                putExtra("code", REQUEST_CODE)
                putExtra("title", title)
                putExtra("content", content)
            }
            val alarmManager = binding.root.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(binding.root.context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun copyContent() {
        val titleText = mainAstroTitleTv.text.toString()
        val contentText = monthlyEventInfoTv.text.toString()

        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", titleText+"\n\n"+contentText))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler(astroEvent: String, astroTitle: String, astroTime: String, astroDate: String) {
        //Log.e("INIT", astroEvent + "/" + astroTitle + "/" + astroTime + "/" + astroDate)
        astroAdapter = AstroRecyclerAdapter(this)
        astroRv.adapter = astroAdapter
        datas.apply { add(AstroItem(astroEvent,astroTime,astroTitle,astroDate)) }
        astroRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        astroRv.setHasFixedSize(true)
        astroAdapter.datas = datas
        astroAdapter.notifyDataSetChanged()

        astroAdapter.setOnAstroClickListener(object: AstroRecyclerAdapter.onAstroClickListener {
            override fun onItemClick(v: View?) {
                val astroClickTitle = v!!.findViewById<TextView>(R.id.astroTitleTv)
                val astroClickEvent = v.findViewById<TextView>(R.id.astroEventTv)
                val query = astroClickTitle.text.takeIf { it.isNotBlank() }?.toString() ?: astroClickEvent.text.takeIf { it.isNotBlank() }?.toString()
                val internetIntent = Intent(Intent.ACTION_WEB_SEARCH)
                internetIntent.putExtra(SearchManager.QUERY, query)
                startActivity(internetIntent)
            }

            override fun onLongClick(v: View?) {
                val astroClickTitle = v!!.findViewById<TextView>(R.id.astroTitleTv)
                val astroClickEvent = v.findViewById<TextView>(R.id.astroEventTv)
                val title = "ToTheStarlight"
                val textData1 = astroClickTitle.text.takeIf { it.isNotBlank() }?.toString() ?: astroClickEvent.text.takeIf { it.isNotBlank() }?.toString()

                val intent = Intent(binding.root.context, MyAlarmReceiver::class.java).apply {
                    putExtra("code", REQUEST_CODE)
                    putExtra("title", title)
                    putExtra("content", textData1)
                }
                val alarmManager = binding.root.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val pendingIntent = PendingIntent.getBroadcast(binding.root.context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent
                )
            }

        })
    }

    private fun makeUrlBuilder1(year: String?, month: String?) {
        //Log.d("parsingLog", "makeUrlBuilder1 실행됨")
        try {
            urlBuilder1.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + BuildConfig.API_KEY)
            urlBuilder1.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")) // 연도
            urlBuilder1.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")) // 월
            urlBuilder1.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("40", "UTF-8")) //모든 데이터 출력
            Log.d("parsingLog", "url1: $urlBuilder1")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun makeUrlBuilder2(year: String?, month: String?) {
        //Log.d("parsingLog", "makeUrlBuilder2 실행됨")
        try {
            urlBuilder2.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + BuildConfig.API_KEY)
            urlBuilder2.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")) // 연도
            urlBuilder2.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")) // 월
            urlBuilder2.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("32", "UTF-8")) //모든 데이터 출력
            Log.d("parsingLog", "url2: $urlBuilder2")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun makeUrlBuilder3(date: String, location: String) {
        //Log.d("parsingLog", "makeUrlBuilder3 실행됨")
        try {
            urlBuilder3.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + BuildConfig.API_KEY)
            urlBuilder3.append("&" + URLEncoder.encode("locdate", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")) // 연도
            urlBuilder3.append("&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8")) // 월
            Log.d("parsingLog", "url3: $urlBuilder3")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun xmlParsing1() {
        try {
            val url = URL(urlBuilder2.toString())

            val task = XMLTask1()
            task.execute(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    private fun xmlParsing2() {
        try {
            val url = URL(urlBuilder1.toString())

            val task = XMLTask2()
            task.execute(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    private fun xmlParsing3() {
        try {
            val url = URL(urlBuilder3.toString())

            val task = XMLTask3()
            task.execute(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class XMLTask1 : AsyncTask<URL?, Void?, Unit?>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg urls: URL?) {
            val myUrl = urls[0]

            try {
                val `is` = myUrl?.openStream()
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()

                parser.setInput(`is`, "UTF8")
                var eventType = parser.eventType

                var tagName: String

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            tagName = parser.name
                            when (tagName) {
                                "lunAge" -> {
                                    parser.next()
                                    lunAge = parser.text
                                    //Log.e("Age:", parser.text)
                                }

                                "solDay" -> {
                                    parser.next()
                                    solDay = parser.text
                                    //Log.e("Day:", parser.text)
                                }

                                "solMonth" -> {
                                    parser.next()
                                    solMonth = parser.text
                                    //Log.e("Month:", parser.text)
                                }

                                "solWeek" -> {
                                    parser.next()
                                    solWeek = parser.text
                                    //Log.e("Week:", parser.text)
                                }

                                "solYear" -> {
                                    parser.next()
                                    solYear = parser.text
                                    //Log.e("Year:", parser.text)
                                }
                            }
                        }

                        XmlPullParser.END_TAG -> {
                            tagName = parser.name
                            if (tagName == "item") {
                                //Log.d("RESULT", solYear+"/"+solMonth+"/"+solDay+"/"+solWeek+"/"+lunAge)
                                lunItems.add(LunItem(lunAge.toString(), solDay.toString(), solMonth.toString(), solWeek.toString(), solYear.toString()))
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @SuppressLint("DiscouragedApi")
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            val calendar = Calendar.getInstance()
            var index = 0
            while (index < lunItems.size) {
                val itemAge = "%.0f".format(lunItems[index].lunAge.toDouble())
                val itemYear = lunItems[index].solYear
                val itemMonth = lunItems[index].solMonth
                val itemDay = lunItems[index].solDay
                val drawableId = resources.getIdentifier("moon_shape${itemAge}", "drawable", packageName)
                val drawable: Drawable? = ContextCompat.getDrawable(this@MainActivity, drawableId)
                calendar.set(itemYear.toInt(), itemMonth.toInt() - 1, itemDay.toInt())
                events.add(EventDay(calendar.clone() as Calendar, drawable!!))
                calendar.add(Calendar.DATE, 1)
                index++
            }
            custom.setEvents(events)

            val date = LocalDate.now()
            val divideDate = date.toString().split("-")
            lunarTv.text = convertSolarToLunar(
                divideDate[0].toInt(),
                divideDate[1].toInt(),
                divideDate[2].toInt()
            )
            for (i in 0 until lunItems.size) {
                if (lunItems[i].solMonth == divideDate[1] && lunItems[i].solDay == divideDate[2]) {
                    moonAgeTv.text = lunItems[i].lunAge
                    val drawableId = resources.getIdentifier(
                        "moon_shape${floor(lunItems[i].lunAge.toDouble()).toInt()}",
                        "drawable",
                        packageName
                    )
                    val drawable: Drawable? =
                        ContextCompat.getDrawable(this@MainActivity, drawableId)
                    moonShapeIv.setImageDrawable(drawable)
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class XMLTask2 : AsyncTask<URL?, Void?, Unit?>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg urls: URL?) {
            val myUrl = urls[0]
            try {
                val `is` = myUrl?.openStream()
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()

                parser.setInput(`is`, "UTF8")
                var eventType = parser.eventType
                var tagName: String?

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            tagName = parser.name
                            when (tagName) {
                                "astroEvent" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    astroEvent = text
                                    //Log.e("astroEvent", text)
                                }

                                "astroTitle" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    astroTitle = text
                                    //Log.e("astroTitle", text)
                                }

                                "astroTime" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    astroTime = text
                                    //Log.e("astroTime", text)
                                }

                                "locdate" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    val subYear = text.substring(0, 4)
                                    val subMonth = text.substring(4, 6)
                                    val subDay = text.substring(6)
                                    val subStringDate = "$subYear-$subMonth-$subDay"
                                    astroDate = subStringDate
                                    //Log.e("locDate", text)
                                }
                            }
                        }

                        XmlPullParser.END_TAG -> {
                            tagName = parser.name
                            if (tagName == "item") {
                                astroItems.add(AstroItem(astroEvent.toString(), astroTime.toString(), astroTitle.toString(), astroDate.toString()))
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            for(i in 0 until astroItems.size) {
                if(i != 0) {
                    initRecycler(astroItems[i].astroEvent.toString(), astroItems[i].astroTitle.toString(), astroItems[i].astroTime.toString(), astroItems[i].astroDate.toString())
                }
            }
            mainAstroTitleTv.text = astroItems[0].astroTitle
            monthlyEventInfoTv.text = astroItems[0].astroEvent
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class XMLTask3 : AsyncTask<URL?, Void?, Unit?>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg urls: URL?) {
            val myUrl = urls[0]
            try {
                val `is` = myUrl?.openStream()
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()

                parser.setInput(`is`, "UTF8")
                var eventType = parser.eventType
                var tagName: String?

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            tagName = parser.name
                            when (tagName) {
                                "moonrise" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    moonRise = text
                                    //Log.e("moonRise", text)
                                }

                                "moonset" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    moonSet = text
                                    //Log.e("moonSet", text)
                                }
                                "sunrise" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    sunRise = text
                                    //Log.e("sunRise", text)
                                }

                                "sunset" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    sunSet = text
                                    //Log.e("sunSet", text)
                                }
                            }
                        }

                        XmlPullParser.END_TAG -> {
                            tagName = parser.name
                            if (tagName == "item") {
                                riseItems.add(RiseItem(sunRise, sunSet, moonRise, moonSet))
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            sunRiseTv.text = riseItems[0].sunRise!!.trim().chunked(2).joinToString(":")
            moonRiseTv.text = riseItems[0].moonRise!!.trim().chunked(2).joinToString(":")
            sunSetTv.text = riseItems[0].sunSet!!.trim().chunked(2).joinToString(":")
            moonSetTv.text = riseItems[0].moonSet!!.trim().chunked(2).joinToString(":")
        }
    }

    private var backPressedTime: Long = 0
    override fun onBackPressed() {
        if(System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        else if(System.currentTimeMillis() <= backPressedTime + 2000) {
            super.onBackPressed()
        }
    }
}