package com.example.prayer

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.others.copyToClipboard
import com.example.others.hideSoftKeyboard
import com.example.others.pasteFromClipboard
import com.example.others.showSoftKeyboard
import java.io.File

val bookmarksKey = mutableListOf<String>() //數字 //adapter
val bookmarksValue = mutableListOf<String>() // 附註
fun readBookmarks() {
    try {
        val string = File(filesDir, "bookmarks.txt").readText().removeSuffix("\r")
        val list = string.split('\n')
        bookmarksKey.clear()
        bookmarksValue.clear()
        for(i in list) {
            val tempList = i.split(';')
            bookmarksKey.add(tempList[0])
            bookmarksValue.add(tempList[1])
        }
    }
    catch(_: Exception) {
        File(filesDir, "bookmarks.txt").writeText("")
        bookmarksKey.clear()
        bookmarksValue.clear()
    }
}
fun writeBookmarks() {
    var string = ""
    for(i in bookmarksKey.indices) {
        string += "${bookmarksKey[i]};${bookmarksValue[i]}"
        if(i != bookmarksKey.lastIndex) {
            string += '\n'
        }
    }
    File(filesDir, "bookmarks.txt").writeText(string)
}

class HomeFragment: Fragment() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var mainActivity: MainActivity
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView
    private lateinit var prayerView: EditText
    private lateinit var rhemaView: EditText

    private val originWebsites = """
            nhentai.net nhentai.net/g/# 6-6
            nhentai.com nhentai.com/g/# 6-6
            nhentai.to nhentai.to/g/# 6-6
            Pixiv pixiv.net/artworks/# 8-10
        """.trimIndent()
    private val adapterList = mutableListOf<String>()
    private var prayer = "" // 數字
    private var rhema = defaultWebsite // 網址
    private var door = 0 // 可能是auto
    private var doorReal = 0 // 絕對不是auto
    private var doorUrlList = mutableListOf(defaultWebsite)
    private var doorSizeList = mutableListOf(arrayOf(0,0))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainActivity = activity as MainActivity

        // Grant Permission
        notificationManager = mainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(!notificationManager.isNotificationPolicyAccessGranted) {
            Toast.makeText(mainActivity, "賦予Prayer勿擾權限後,\n請關閉此設定頁面並重新開啟Prayer", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){}.launch(intent)
        }

        readBookmarks()

        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rhemaView = view.findViewById(R.id.rhema)
        rhemaView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                rhema = s.toString()
            }
        })
        rhemaView.setOnLongClickListener {
            rhemaView.setText("")
            return@setOnLongClickListener true
        }

        val bookmarkView = view.findViewById<ImageView>(R.id.bookmark)
        val originBookmark = bookmarkView.drawable
        bookmarkView.setOnClickListener {
            hideSoftKeyboard(mainActivity, rhemaView.windowToken)

            if(bookmarkView.drawable == originBookmark) { // make
                val bookmarkDialogView = View.inflate(mainActivity, R.layout.alertdialog_add_bookmark, null)
                val alertDialog = AlertDialog.Builder(mainActivity).setCancelable(false).setView(bookmarkDialogView).show()

                bookmarkDialogView.findViewById<AppCompatButton>(R.id.cancel).setOnClickListener {
                    alertDialog.dismiss()
                }
                bookmarkDialogView.findViewById<AppCompatButton>(R.id.done).setOnClickListener {
                    alertDialog.dismiss()
                    val commentView = bookmarkDialogView.findViewById<EditText>(R.id.comment)
                    bookmarkView.setImageResource(R.drawable.bookmark_on)
                    bookmarksKey.add(0,prayer)
                    bookmarksValue.add(0,commentView.text.toString())
                    writeBookmarks()
                    prayerView.clearFocus()
                    hideSoftKeyboard(mainActivity, prayerView.windowToken)
                }
            }
            else { // delete
                bookmarkView.setImageDrawable(originBookmark)
                bookmarksKey.removeFirst()
                bookmarksValue.removeFirst()
                writeBookmarks()
            }
        }
        bookmarkView.setOnLongClickListener {
            val bookmarkDialogView = View.inflate(mainActivity, R.layout.alertdialog_add_bookmark, null)
            val alertDialog = AlertDialog.Builder(mainActivity).setCancelable(false).setView(bookmarkDialogView).show()
            val commentView = bookmarkDialogView.findViewById<EditText>(R.id.comment)
            var index: Int? = null

            if(bookmarkView.drawable != originBookmark) { // edit
                index = bookmarksKey.indexOf(prayer)
                commentView.setText(bookmarksValue[index])
            }

            bookmarkDialogView.findViewById<AppCompatButton>(R.id.cancel).setOnClickListener {
                alertDialog.dismiss()
            }
            bookmarkDialogView.findViewById<AppCompatButton>(R.id.done).setOnClickListener {
                alertDialog.dismiss()
                if(index == null) { // make
                    bookmarksKey.add(0,prayer)
                    bookmarksValue.add(0,commentView.text.toString())
                }
                else { // edit
                    bookmarksValue[index] = commentView.text.toString()
                }
                writeBookmarks()
                prayerView.clearFocus()
                hideSoftKeyboard(mainActivity, prayerView.windowToken)
            }

            return@setOnLongClickListener true
        }

        prayerView = view.findViewById(R.id.prayer)
        prayerView.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                prayer = s.toString()
                if(prayer in bookmarksKey && prayer != "") {
                    bookmarkView.setImageResource(R.drawable.bookmark_on)
                }
                else {
                    bookmarkView.setImageDrawable(originBookmark)
                }
                generateRhema()
            }
        })
        prayerView.setOnLongClickListener {
            if(prayerView.text.toString() == "") { //帖上
                prayerView.setText(pasteFromClipboard(mainActivity))
                hideSoftKeyboard(mainActivity, prayerView.windowToken)
                generateRhema()
                prayerView.clearFocus()
            }
            else { //清空
                prayerView.hint = ""
                prayerView.setText("")
                showSoftKeyboard(mainActivity, prayerView)
            }
            return@setOnLongClickListener true
        }
        prayerView.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                prayerView.hint = ""
            }
        }

        val dnd = view.findViewById<ImageView>(R.id.dnd)
        val originDND = dnd.drawable
        if(notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE) {
            dnd.setImageResource(R.drawable.dnd_on)
        }
        dnd.setOnClickListener {
            if(notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) { // turn on
                dnd.setImageResource(R.drawable.dnd_on)
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            }
            else { // turn off
                dnd.setImageDrawable(originDND)
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }

        readWebsites()

        listView = view.findViewById(R.id.list_view)
        adapter = object: ArrayAdapter<String>(mainActivity, R.layout.websites_list, adapterList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var listItemView = convertView
                if(convertView == null) {
                    listItemView = LayoutInflater.from(mainActivity).inflate(R.layout.websites_list, parent, false)
                }

                val radioButton = listItemView!!.findViewById<RadioButton>(R.id.radio_button)
                radioButton.text = getItem(position)

                radioButton.isChecked = (position == door)
                radioButton.setOnClickListener {
                    door = position
                    notifyDataSetChanged()
                    generateRhema()
                }
                radioButton.setOnLongClickListener {
                    val layout = View.inflate(mainActivity, R.layout.alertdialog_edit_websites, null)
                    val editText = layout.findViewById<EditText>(R.id.edit_text)
                    editText.setText(File(mainActivity.filesDir, "websites.txt").readText())

                    AlertDialog.Builder(mainActivity).setCancelable(false).setView(layout)
                        .setNeutralButton("Reset") { _, _ ->
                            File(mainActivity.filesDir, "websites.txt").delete()
                            readWebsites()
                            mainActivity.restart()
                        }
                        .setNegativeButton("Cancel",null)
                        .setPositiveButton("Done") { _, _ ->
                            File(mainActivity.filesDir, "websites.txt").writeText(editText.text.toString())
                            readWebsites()
                            mainActivity.restart()
                        }
                        .show()

                    return@setOnLongClickListener true
                }

                listItemView.setOnClickListener {
                    radioButton.isChecked = (position == door)
                    door = position
                    notifyDataSetChanged()
                    generateRhema()
                }

                return listItemView
            }
        }
        listView.adapter = adapter
        view.findViewById<AppCompatButton>(R.id.go_webview).setOnClickListener {
            (activity as MainActivity).replaceNowFragmentWith(WebFragment(rhema))
        }
        view.findViewById<AppCompatButton>(R.id.go_default).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.$rhema"))
            startActivity(intent)
        }

        view.findViewById<ImageView>(R.id.copy).setOnClickListener {
            copyToClipboard(mainActivity, rhema)
        }
        view.findViewById<ImageButton>(R.id.history).setOnClickListener {
            comingSoon()
        }
        view.findViewById<ImageButton>(R.id.bookmarks_library).setOnClickListener {
            mainActivity.replaceNowFragmentWith(BookmarksLibraryFragment())
        }
        view.findViewById<ImageView>(R.id.settings).setOnClickListener {
            comingSoon()
        }
        view.findViewById<View>(R.id.view).setOnClickListener {
            if(prayerView.text.toString() == "") {
                prayerView.hint = getString(R.string.app_name)
            }
            prayerView.clearFocus()
            rhemaView.clearFocus()
            hideSoftKeyboard(mainActivity, prayerView.windowToken)
            hideSoftKeyboard(mainActivity, rhemaView.windowToken)
        }
    }

    private fun generateRhema() {
        doorReal = door
        var isPrayer = false
        when(door) {
            0 ->
                for(i in doorSizeList.indices) {
                    if(doorSizeList[i][0] <= prayer.length && prayer.length <= doorSizeList[i][1]) {
                        rhema = doorUrlList[i].replace("#",prayer)
                        isPrayer = true
                        break
                    }
                }
            else ->
                if(doorSizeList[door][0] <= prayer.length && prayer.length <= doorSizeList[door][1]) {
                    rhema = doorUrlList[door].replace("#",prayer)
                    isPrayer = true
                }
        }

        if(!isPrayer) {
            rhema = defaultWebsite
        }
        rhemaView.setText(rhema)
    }
    private fun readWebsites() {
        var string = originWebsites
        try {
            string = File(mainActivity.filesDir, "websites.txt").readText().removeSuffix("\r")
        }
        catch(_: Exception) {
            File(mainActivity.filesDir, "websites.txt").writeText(string)
        }

        try {
            val list = string.split('\n')

            adapterList.clear()
            adapterList.add("AUTO")
            for(i in list) {
                val splitList = i.split(' ')
                adapterList.add(splitList[0])
                doorUrlList.add(splitList[1])

                val sizeList = splitList[2].split('-')
                doorSizeList.add(arrayOf(sizeList[0].toInt(), sizeList[1].toInt()))
            }
        }
        catch(e: Exception) {
            File(mainActivity.filesDir, "websites.txt").writeText(originWebsites)
            Toast.makeText(mainActivity, "Due to websites.txt format error\nIt has been reset", Toast.LENGTH_LONG).show()
        }
    }



    private fun comingSoon() {
        Toast.makeText(mainActivity, "尚未開放ㄛ", Toast.LENGTH_SHORT).show()
    }
}