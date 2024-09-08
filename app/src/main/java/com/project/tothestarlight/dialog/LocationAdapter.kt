package com.project.tothestarlight.dialog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.tothestarlight.R

class LocationAdapter(var locationList: List<String>, private val onLocationSelected: (String) -> Unit) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(), Filterable {
    var filteredList: List<String> = locationList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationNameTextView: TextView = itemView.findViewById(R.id.locationTv)

        fun bind(location: String) {
            locationNameTextView.text = location
            itemView.setOnClickListener {
                onLocationSelected(location)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""

                filteredList = if (query.isEmpty()) {
                    locationList
                } else {
                    locationList.filter {
                        it.lowercase().contains(query)
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<String>
                notifyDataSetChanged()
            }
        }
    }
}


