package com.example.prayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.custom.itLog

class BookmarksLibraryFragment(): Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fragment_bookmarks_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listView = view.findViewById<ListView>(R.id.list_view)
        adapter = object: ArrayAdapter<String>(mainActivity, R.layout.bookmarks_library_list, bookmarksKey) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var listItemView = convertView
                if(convertView == null) {
                    listItemView = LayoutInflater.from(mainActivity).inflate(R.layout.bookmarks_library_list, parent, false)
                }

                readBookmarks()
                itLog(getItem(position))

                val keyView = listItemView!!.findViewById<TextView>(R.id.key)
                keyView.text = getItem(position)

                val valueView = listItemView.findViewById<TextView>(R.id.value)
                valueView.text = bookmarksValue[position]

                return listItemView
            }
        }
        listView.adapter = adapter
    }
}