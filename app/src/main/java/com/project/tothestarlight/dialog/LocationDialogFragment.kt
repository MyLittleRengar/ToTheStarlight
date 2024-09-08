package com.project.tothestarlight.dialog

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.tothestarlight.R


class LocationDialogFragment : DialogFragment() {

    private lateinit var adapter: LocationAdapter
    private lateinit var searchView: SearchView
    private lateinit var preference: SharedPreferences

    var listener: DialogDismissListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_location_dialog, container, false)

        preference = requireContext().getSharedPreferences("location", 0)

        val locations = resources.getStringArray(R.array.location).toList()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        adapter = LocationAdapter(locations) { selectedLocation ->
            val editor = preference.edit()
            editor.putString("location", selectedLocation)
            editor.apply()
            dismiss()
        }

        val gridLayoutManager = GridLayoutManager(context, 9)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = adapter.filteredList[position]
                return when {
                    item.length <= 2 -> 2
                    item.length == 3 -> 3
                    item.length == 4 -> 3
                    item.length == 5 -> 4
                    item.length == 6 -> 4
                    item.length == 7 -> 4
                    else -> 5
                }
            }
        }

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter

        setupSearchView()

        return view
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog ?: return
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.9).toInt(), (resources.displayMetrics.heightPixels * 0.6).toInt())
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDialogDismissed()
    }
}


