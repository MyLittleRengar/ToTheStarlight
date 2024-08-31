package com.project.tothestarlight

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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

    private var lunAge: String? = null
    private var solDay: String? = null
    private var solMonth: String? = null
    private var solWeek: String? = null
    private var solYear: String? = null

    private var astroEvent: String? = null
    private var astroTitle: String? = null
    private var astroTime: String? = null
    private var astroDate: String? = null

    private lateinit var urlBuilder1: StringBuilder
    private lateinit var urlBuilder2: StringBuilder

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
        monthlyEventInfoTv = findViewById(R.id.monthlyEventInfoTv)
        mainAstroTitleTv = findViewById(R.id.mainAstroTitleTv)
        monthlyEventTv = findViewById(R.id.monthlyEventTv)
        copyIv = findViewById(R.id.copyIv)
        astroRv = findViewById(R.id.astroRv)

        val dateSplit = getCurrentDate().split("-")

        urlBuilder1 = StringBuilder(BuildConfig.API_ASTRO)
        urlBuilder2 = StringBuilder(BuildConfig.API_MOON)
        makeUrlBuilder1(dateSplit[0], dateSplit[1])
        makeUrlBuilder2(dateSplit[0], dateSplit[1])
        xmlParsing1()
        xmlParsing2()

        mainDateTv.text = dateSplit[0] + "년" + dateSplit[1] + "월"

        settingIv.setOnClickListener {
            val dlg = CustomLocationDialogAdapter(this@MainActivity)
            dlg.setOnAcceptClickedListener { location ->
                Log.e("12", location)
            }
            dlg.show()
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
                val month = clickedDay.get(Calendar.MONTH) + 1
                val day = if (clickedDay.get(Calendar.DAY_OF_MONTH) < 10) {
                    "0" + clickedDay.get(Calendar.DAY_OF_MONTH)
                } else {
                    clickedDay.get(Calendar.DAY_OF_MONTH).toString()
                }
                val convert = convertSolarToLunar(year, month, day.toInt())

                lunarTv.text = convert

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

        custom.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val calendarToday = custom.currentPageDate
                val year = calendarToday.get(GregorianCalendar.YEAR).toString()
                val month = calendarToday.get(GregorianCalendar.MONTH) + 1
                val convertMonth = month.toString().padStart(2, '0')
                val day = calendarToday.get(GregorianCalendar.DAY_OF_MONTH)
                Log.e("Test", "년: $year, 월: $convertMonth, 일: $day")
                urlBuilder2.apply {
                    clear()
                    append(BuildConfig.API_MOON)
                }
                lunItems.clear()
                makeUrlBuilder2(year, convertMonth)
                xmlParsing1()
            }
        })

        custom.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val calendarToday = custom.currentPageDate
                val year = calendarToday.get(GregorianCalendar.YEAR).toString()
                val month = calendarToday.get(GregorianCalendar.MONTH) + 1
                val convertMonth = month.toString().padStart(2, '0')
                val day = calendarToday.get(GregorianCalendar.DAY_OF_MONTH)
                Log.e("Test", "년: $year, 월: $convertMonth, 일: $day")
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
        Log.e("INIT", astroEvent + "/" + astroTitle + "/" + astroTime + "/" + astroDate)
        astroAdapter = AstroRecyclerAdapter(this)
        astroRv.adapter = astroAdapter
        datas.apply { add(AstroItem(astroEvent,astroTime,astroTitle,astroDate)) }
        astroRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        astroRv.setHasFixedSize(true)
        astroAdapter.datas = datas
        astroAdapter.notifyDataSetChanged()
    }

    private fun makeUrlBuilder1(year: String?, month: String?) {
        Log.d("parsingLog", "makeUrlBuilder1 실행됨")
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
        Log.d("parsingLog", "makeUrlBuilder2 실행됨")
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
                                lunItems.add(
                                    LunItem(
                                        lunAge.toString(),
                                        solDay.toString(),
                                        solMonth.toString(),
                                        solWeek.toString(),
                                        solYear.toString()
                                    )
                                )
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
                val drawableId =
                    resources.getIdentifier("moon_shape${itemAge}", "drawable", packageName)
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
            monthlyEventTv.text = divideDate[1]
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
            val items = ArrayList<AstroItem>()
            try {
                items.clear()
                val `is` = myUrl?.openStream()
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()

                parser.setInput(`is`, "UTF8")
                var eventType = parser.eventType
                var tagName: String?

                var cnt = 0
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
                                if(cnt != 0) {
                                    Log.d("RESULT", astroEvent + "/" + astroTitle + "/" + astroTime + "/" + astroDate)
                                    initRecycler(astroEvent.toString(), astroTitle.toString(), astroTime.toString(), astroDate.toString())
                                }
                                else {
                                    astroItems.add(AstroItem(astroEvent.toString(), astroTime.toString(), astroTitle.toString(), astroDate.toString()))
                                    cnt++
                                }
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

            mainAstroTitleTv.text = astroItems[0].astroTitle
            monthlyEventInfoTv.text = astroItems[0].astroEvent
        }
    }
}
    // TODO: 출몰 시각 정보 추가시, 지역 설정 및 지역 저장(ShardPreference) 사용, 초기 화면은 오늘 날짜의 일출몰 시각
    // TODO: 날짜 클릭시, 클릭한 날짜 일출몰 시각, 위치TextView 저장된 지역에 맞게 변환. 설정 버튼 -> 지역 변환 -> 데이터 저장
    // TODO: 천문 현상 정보 추가