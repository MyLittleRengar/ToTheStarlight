package com.project.tothestarlight.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.tothestarlight.R
import java.text.SimpleDateFormat
import java.util.Date

class AstroRecyclerAdapter(private val context: Context) : RecyclerView.Adapter<AstroRecyclerAdapter.ViewHolder>() {
    private var astroClickListener: OnAstroClickListener? = null
    var datas = mutableListOf<AstroItem>()

    interface OnAstroClickListener {
        fun onItemClick(v: View?)
        fun onLongClick(v: View?)
    }

    fun setOnAstroClickListener(listener: OnAstroClickListener) {
        this.astroClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.astro_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(datas[position])

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var astroTitleTv: TextView = itemView.findViewById(R.id.astroTitleTv)
        private var astroEventTv: TextView = itemView.findViewById(R.id.astroEventTv)
        private var astroTimeTv: TextView = itemView.findViewById(R.id.astroTimeTv)
        private var astroDateTv: TextView = itemView.findViewById(R.id.astroDateTv)
        private var astroCountdownTv: TextView = itemView.findViewById(R.id.astroCountdownTv)
        private var countDownTimer: CountDownTimer? = null  // 타이머 참조 변수

        fun bind(item: AstroItem) {
            astroTitleTv.text = item.astroTitle
            astroEventTv.text = item.astroEvent
            astroTimeTv.text = item.astroTime
            astroDateTv.text = item.astroDate

            // 기존 타이머 취소
            countDownTimer?.cancel()

            val targetDate = parseDateTime(item.astroDate ?: "", item.astroTime)
            val remainingTimeInMillis = calculateRemainingTime(targetDate)

            // 새로운 타이머 생성 및 시작
            countDownTimer = object : CountDownTimer(remainingTimeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    astroCountdownTv.text = formatTime(millisUntilFinished)
                }

                override fun onFinish() {
                    astroCountdownTv.text = "완료된 이벤트"
                }
            }
            countDownTimer?.start()

            if (item.astroTitle == "") {
                astroTitleTv.visibility = View.GONE
            }
            if (item.astroEvent == "") {
                astroEventTv.visibility = View.GONE
            }
            if (item.astroTime == "") {
                astroTimeTv.visibility = View.GONE
            }

            itemView.setOnClickListener { view ->
                astroClickListener?.onItemClick(view)
            }

            itemView.setOnLongClickListener { view ->
                astroClickListener?.onLongClick(view)
                true
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return if (days > 0) {
            String.format("%d일 %02d:%02d:%02d", days, hours % 24, minutes % 60, seconds % 60)
        } else {
            String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60)
        }
    }

    private fun calculateRemainingTime(targetDate: Date): Long {
        val currentTime = System.currentTimeMillis()
        return targetDate.time - currentTime
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseDateTime(astroDate: String, astroTime: String?): Date {
        val dateFormat = if (!astroTime.isNullOrEmpty()) {
            SimpleDateFormat("yyyy-MM-dd HH:mm")
        } else {
            SimpleDateFormat("yyyy-MM-dd")
        }

        return if (!astroTime.isNullOrEmpty()) {
            dateFormat.parse("$astroDate $astroTime") ?: Date()
        } else {
            dateFormat.parse(astroDate) ?: Date()
        }
    }
}
