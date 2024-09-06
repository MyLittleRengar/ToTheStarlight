package com.project.tothestarlight.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.tothestarlight.R

class AstroRecyclerAdapter(private val context: Context): RecyclerView.Adapter<AstroRecyclerAdapter.ViewHolder>() {
    private var astroClickListener: onAstroClickListener? = null
    var datas = mutableListOf<AstroItem>()

    interface onAstroClickListener {
        fun onItemClick(v: View?)
        fun onLongClick(v: View?)
    }

    fun setOnAstroClickListener(listener: onAstroClickListener) {
        this.astroClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.astro_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(datas[position])

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
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

            itemView.setOnClickListener { view ->
                astroClickListener?.onItemClick(view)
            }

            itemView.setOnLongClickListener { view ->
                astroClickListener?.onLongClick(view)

                true
            }
        }
    }
}