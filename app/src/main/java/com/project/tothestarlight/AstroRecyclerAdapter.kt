package com.project.tothestarlight

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AstroRecyclerAdapter(val context: Context): RecyclerView.Adapter<AstroRecyclerAdapter.ViewHolder>() {
    var datas = mutableListOf<AstroItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AstroRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.astro_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(datas[position])

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var astroLl: LinearLayout = itemView.findViewById(R.id.astroLl)
        private var astroTitleTv: TextView = itemView.findViewById(R.id.astroTitleTv)
        private var astroEventTv: TextView = itemView.findViewById(R.id.astroEventTv)
        private var astroTimeTv: TextView = itemView.findViewById(R.id.astroTimeTv)
        private var astroDateTv: TextView = itemView.findViewById(R.id.astroDateTv)

        fun bind(item: AstroItem) {
            astroTitleTv.text = item.astroTitle
            astroEventTv.text = item.astroEvent
            astroTimeTv.text = item.astroTime
            astroDateTv.text = item.astroDate

            if(item.astroTitle == "") {
                astroTitleTv.visibility = View.GONE
            }
            if(item.astroEvent == ""){
                astroEventTv.visibility = View.GONE
            }
            if(item.astroTime == ""){
                astroTimeTv.visibility = View.GONE
            }
            //길게 클릭시 알림 등록
        }
    }
}