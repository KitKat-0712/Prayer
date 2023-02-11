package com.example.prayer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class BookmarksLibraryFragment: Fragment() {
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

                mainActivity.readBookmarks()

                val keyView = listItemView!!.findViewById<TextView>(R.id.key)
                keyView.text = getItem(position)

                val valueView = listItemView.findViewById<TextView>(R.id.value)
                valueView.text = bookmarksValue[position]

                listItemView.setOnClickListener {
                    val layout = View.inflate(mainActivity, R.layout.alertdialog_edit_bookmark, null)
                    val keyEditView = layout.findViewById<EditText>(R.id.key_edittext)
                    val valueEditView = layout.findViewById<EditText>(R.id.value_edittext)
                    keyEditView.setText(bookmarksKey[position])
                    valueEditView.setText(bookmarksValue[position])

                    AlertDialog.Builder(mainActivity).setCancelable(false).setView(layout)
                        .setNegativeButton("Cancel",null)
                        .setNeutralButton("Delete") { _, _ ->
                            bookmarksKey.removeAt(position)
                            bookmarksValue.removeAt(position)
                            mainActivity.writeBookmarks()
                            notifyDataSetChanged()
                        }
                        .setPositiveButton("Done") { _, _ ->
                            bookmarksKey[position] = keyEditView.text.toString()
                            bookmarksValue[position] = valueEditView.text.toString()
                            mainActivity.writeBookmarks()
                            notifyDataSetChanged()
                        }
                        .show()
                }

                return listItemView
            }
        }
        listView.adapter = adapter
    }
}