package com.project.tothestarlight

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.CalendarWeekDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.project.tothestarlight.dialog.CircleProgressDialog
import com.project.tothestarlight.recycler.AstroItem
import com.project.tothestarlight.recycler.AstroRecyclerAdapter
import com.project.tothestarlight.recycler.LunItem
import com.project.tothestarlight.recycler.RiseItem
import com.project.tothestarlight.recycler.WeatherItem
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
    private lateinit var monthlyEventLl: LinearLayout
    private var openType: Boolean = true

    private val loadingDialog = CircleProgressDialog()

    val events = mutableListOf<EventDay>()
    private var lunItems = mutableListOf<LunItem>()

    private lateinit var astroAdapter: AstroRecyclerAdapter
    private var datas = mutableListOf<AstroItem>()
    private var astroItems = mutableListOf<AstroItem>()
    private var riseItems = mutableListOf<RiseItem>()
    private var weatherItems = mutableListOf<WeatherItem>()

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

    private var rnSt3Am: String? = null
    private var rnSt3Pm: String? = null
    private var rnSt4Am: String? = null
    private var rnSt4Pm: String? = null
    private var rnSt5Am: String? = null
    private var rnSt5Pm: String? = null
    private var rnSt6Am: String? = null
    private var rnSt6Pm: String? = null
    private var rnSt7Am: String? = null
    private var rnSt7Pm: String? = null
    private var rnSt8: String? = null
    private var rnSt9: String? = null
    private var rnSt10: String? = null
    private var wf3Am: String? = null
    private var wf3Pm: String? = null
    private var wf4Am: String? = null
    private var wf4Pm: String? = null
    private var wf5Am: String? = null
    private var wf5Pm: String? = null
    private var wf6Am: String? = null
    private var wf6Pm: String? = null
    private var wf7Am: String? = null
    private var wf7Pm: String? = null
    private var wf8: String? = null
    private var wf9: String? = null
    private var wf10: String? = null

    private lateinit var urlBuilder1: StringBuilder
    private lateinit var urlBuilder2: StringBuilder
    private lateinit var urlBuilder3: StringBuilder
    private lateinit var urlBuilder4: StringBuilder

    private lateinit var preference: SharedPreferences
    private var selectedLocation = ""
    private var selectedWeatherLocation = ""

    private var isMoonImageLoaded = false
    private var isDataLoaded = false
    private var isRecyclerViewUpdated = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

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
        monthlyEventLl = findViewById(R.id.monthlyEventLl)

        preference = getSharedPreferences("location", 0)

        //TODO 날씨 데이터 갱신 분류를 위한 코드 ing,,
        //TODO val testing = LocalDateTime.now()
        //TODO val testTimeFormat = testing.format(DateTimeFormatter.ofPattern("HH:mm"))
        //TODO val testDateFormat = testing.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        //TODO val testFormat = testing.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
        //TODO Log.e("!!!!!", testFormat)

        val maxCalendar = Calendar.getInstance()
         maxCalendar.add(Calendar.MONTH, 3)
        maxCalendar.set(Calendar.DAY_OF_MONTH, maxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        custom.setMaximumDate(maxCalendar)

        if(locationTv.text == "위치") {
            selectedLocation = preference.getString("location", "").toString()
            if(selectedLocation.isBlank()) {
                selectedLocation = "서울"
                locationTv.text = selectedLocation
            }
            else {
                locationTv.text = selectedLocation
            }
        }

        selectedWeatherLocation = preference.getString("weatherLocation", "").toString()

        val dateSplit = getCurrentDate().split("-")
        val currentDate = dateSplit[0]+dateSplit[1]+dateSplit[2]

        urlBuilder1 = StringBuilder(BuildConfig.API_ASTRO)
        urlBuilder2 = StringBuilder(BuildConfig.API_MOON)
        urlBuilder3 = StringBuilder(BuildConfig.API_RISE)
        urlBuilder4 = StringBuilder(BuildConfig.API_WEATHER)
        makeUrlBuilder1(dateSplit[0], dateSplit[1])
        makeUrlBuilder2(dateSplit[0], dateSplit[1])
        makeUrlBuilder3(currentDate, selectedLocation)
        makeUrlBuilder4("202409120600", getLocationCode(selectedWeatherLocation)!!)
        xmlParsing1()
        xmlParsing2()
        xmlParsing3()
        xmlParsing4()

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
                if(showLoadingDialog()) {
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
                else {
                    val currentMonth = custom.currentPageDate
                    custom.setDate(currentMonth)
                }
            }
        })

        //저번 달 클릭
        custom.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChange() {
                if(showLoadingDialog()) {
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
                else {
                    val currentMonth = custom.currentPageDate
                    custom.setDate(currentMonth)
                }
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

        val selectedDayPref = getSharedPreferences("firstDay", 0)
        val selectedFirstDay = selectedDayPref.getString("firstDay", "").toString()
        if(selectedFirstDay.isNotEmpty()) {
            val firstDayOfWeek = getCalendarWeekDay(selectedFirstDay)
            custom.setFirstDayOfWeek(firstDayOfWeek)
        }
        else {
            custom.setFirstDayOfWeek(CalendarWeekDay.SUNDAY)
        }
    }

    private fun getCalendarWeekDay(dayName: String): CalendarWeekDay {
        return when (dayName) {
            "MONDAY" -> CalendarWeekDay.MONDAY
            "TUESDAY" -> CalendarWeekDay.TUESDAY
            "WEDNESDAY" -> CalendarWeekDay.WEDNESDAY
            "THURSDAY" -> CalendarWeekDay.THURSDAY
            "FRIDAY" -> CalendarWeekDay.FRIDAY
            "SATURDAY" -> CalendarWeekDay.SATURDAY
            "SUNDAY" -> CalendarWeekDay.SUNDAY
            else -> CalendarWeekDay.MONDAY
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

        astroAdapter.setOnAstroClickListener(object: AstroRecyclerAdapter.OnAstroClickListener {
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
                val textData1 = astroClickTitle.text.takeIf { it.isNotBlank() }?.toString() ?: astroClickEvent.text.takeIf { it.isNotBlank() }?.toString()
                createAlertDialog(textData1!!, v)
            }
        })
    }

    private fun createAlertDialog(event: String, v: View?) {
        val builder  = AlertDialog.Builder(this)
        builder.setTitle("[ $event ] 에 대한 \n알림을 설정하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                val astroDate = v!!.findViewById<TextView>(R.id.astroDateTv).text.toString()
                val astroTimes = v.findViewById<TextView>(R.id.astroTimeTv).text.toString()
                val splitDate = astroDate.split("-")
                val year = splitDate[0].toInt()
                val month = splitDate[1].replace("0", "")
                val transMonth = getMonthFromString(month)
                val day = splitDate[2].replace("0", "").toInt()

                val title = "ToTheStarlight  🪐"
                val content = "오늘 ${astroTimes}에 이벤트가 있어요  🔭"

                AlarmUtils.setAlarmAt(this, year, transMonth,day, REQUEST_CODE, title, content)
                Toast.makeText(this, "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소") {_, _ -> }
        builder.show()
    }

    private fun getMonthFromString(monthString: String): Int {
        return when (monthString.toInt()) {
            1 -> Calendar.JANUARY
            2 -> Calendar.FEBRUARY
            3 -> Calendar.MARCH
            4 -> Calendar.APRIL
            5 -> Calendar.MAY
            6 -> Calendar.JUNE
            7 -> Calendar.JULY
            8 -> Calendar.AUGUST
            9 -> Calendar.SEPTEMBER
            10 -> Calendar.OCTOBER
            11 -> Calendar.NOVEMBER
            12 -> Calendar.DECEMBER
            else -> throw IllegalArgumentException("Invalid month: $monthString")
        }
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
            urlBuilder3.append("&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8")) // 위치
            Log.d("parsingLog", "url3: $urlBuilder3")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun makeUrlBuilder4(date: String, location: String) {
        //Log.d("parsingLog", "makeUrlBuilder4 실행됨")
        try {
            urlBuilder4.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + BuildConfig.API_KEY)
            urlBuilder4.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")) // 페이지 수
            urlBuilder4.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")) // 결과 수
            urlBuilder4.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("XML", "UTF-8")) // 요청 자료 형식
            urlBuilder4.append("&" + URLEncoder.encode("regId", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8")) // 지역 코드
            urlBuilder4.append("&" + URLEncoder.encode("tmFc", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")) // 날짜, 시간
            Log.d("parsingLog", "url4: $urlBuilder4")

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

    private fun xmlParsing4() {
        try {
            val url = URL(urlBuilder4.toString())

            val task = XMLTask4()
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
            lunItems.forEach { item ->
                val itemAge = "%.0f".format(item.lunAge.toDouble())
                val itemYear = item.solYear.toInt()
                val itemMonth = item.solMonth.toInt() - 1
                val itemDay = item.solDay.toInt()
                val drawableId = resources.getIdentifier("moon_shape$itemAge", "drawable", packageName)
                val drawable: Drawable? = ContextCompat.getDrawable(this@MainActivity, drawableId)
                calendar.set(itemYear, itemMonth, itemDay)
                events.add(EventDay(calendar.clone() as Calendar, drawable!!))
                calendar.add(Calendar.DATE, 1)
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

            isMoonImageLoaded = true
            checkIfAllTasksCompleted()
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

            if(astroItems.isNotEmpty()) {
                monthlyEventLl.visibility = View.VISIBLE
                astroRv.visibility = View.VISIBLE

                for(i in 0 until astroItems.size) {
                    if(i != 0) {
                        initRecycler(astroItems[i].astroEvent.toString(), astroItems[i].astroTitle.toString(), astroItems[i].astroTime.toString(), astroItems[i].astroDate.toString())
                    }
                }
                mainAstroTitleTv.text = astroItems[0].astroTitle
                monthlyEventInfoTv.text = astroItems[0].astroEvent
            }
            else {
                astroItems.clear()
                monthlyEventLl.visibility = View.GONE
                astroRv.visibility = View.GONE
            }

            isRecyclerViewUpdated = true
            checkIfAllTasksCompleted()
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

            isDataLoaded = true
            checkIfAllTasksCompleted()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class XMLTask4 : AsyncTask<URL?, Void?, Unit?>() {

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
                                "rnSt3Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt3Am = text
                                    //Log.e("moonRise", text)
                                }

                                "rnSt3Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt3Pm = text
                                    //Log.e("moonSet", text)
                                }
                                "rnSt4Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt4Am = text
                                    //Log.e("sunRise", text)
                                }

                                "rnSt4Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt4Pm = text
                                    //Log.e("sunSet", text)
                                }
                                "rnSt5Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt5Am = text
                                    //Log.e("moonRise", text)
                                }

                                "rnSt5Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt5Pm = text
                                    //Log.e("moonSet", text)
                                }
                                "rnSt6Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt6Am = text
                                    //Log.e("sunRise", text)
                                }

                                "rnSt6Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt6Pm = text
                                    //Log.e("sunSet", text)
                                }

                                "rnSt7Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt7Am = text
                                    //Log.e("moonRise", text)
                                }

                                "rnSt7Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt7Pm = text
                                    //Log.e("moonSet", text)
                                }
                                "rnSt8" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt8 = text
                                    //Log.e("sunRise", text)
                                }

                                "rnSt9" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt9 = text
                                    //Log.e("sunSet", text)
                                }

                                "rnSt10" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    rnSt10 = text
                                    //Log.e("moonRise", text)
                                }

                                "wf3Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf3Am = text
                                    //Log.e("moonSet", text)
                                }
                                "wf3Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf3Pm = text
                                    //Log.e("sunRise", text)
                                }

                                "wf4Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf4Am = text
                                    //Log.e("sunSet", text)
                                }
                                "wf4Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf4Pm = text
                                    //Log.e("moonRise", text)
                                }

                                "wf5Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf5Am = text
                                    //Log.e("moonSet", text)
                                }
                                "wf5Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf5Pm = text
                                    //Log.e("sunRise", text)
                                }

                                "wf6Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf6Am = text
                                    //Log.e("sunSet", text)
                                }

                                "wf6Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf6Pm = text
                                    //Log.e("moonRise", text)
                                }

                                "wf7Am" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf7Am = text
                                    //Log.e("moonSet", text)
                                }
                                "wf7Pm" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf7Pm = text
                                    //Log.e("sunRise", text)
                                }

                                "wf8" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf8 = text
                                    //Log.e("sunSet", text)
                                }

                                "wf9" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf9 = text
                                    //Log.e("moonRise", text)
                                }

                                "wf10" -> {
                                    parser.next()
                                    val text = parser.text ?: ""
                                    wf10 = text
                                    //Log.e("moonSet", text)
                                }
                            }
                        }

                        XmlPullParser.END_TAG -> {
                            tagName = parser.name
                            if (tagName == "item") {
                                weatherItems.add(WeatherItem(rnSt3Am,rnSt3Pm,rnSt4Am,rnSt4Pm,rnSt5Am,rnSt5Pm,rnSt6Am,rnSt6Pm,rnSt7Am,rnSt7Pm,rnSt8,rnSt9,rnSt10,wf3Am,wf3Pm,wf4Am,wf4Pm,wf5Am,wf5Pm,wf6Am,wf6Pm,wf7Am,wf7Pm,wf8,wf9,wf10))
                                Log.e("@@@@@", weatherItems.toString())
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

            isDataLoaded = true
            checkIfAllTasksCompleted()
        }
    }

    private var backPressedTime: Long = 0
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if(System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        else if(System.currentTimeMillis() <= backPressedTime + 2000) {
            super.onBackPressed()
        }
    }

    private fun showLoadingDialog(): Boolean {
        val existingDialog = supportFragmentManager.findFragmentByTag("CircleProgressDialog")
        return existingDialog == null
    }

    private fun checkIfAllTasksCompleted() {
        if (isMoonImageLoaded && isDataLoaded && isRecyclerViewUpdated) {
            loadingDialog.dismiss()
        }
    }
}