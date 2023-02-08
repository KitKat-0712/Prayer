package com.example.prayer

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
import com.example.custom.itLog
import com.example.others.copyToClipboard
import com.example.others.hideSoftKeyboard
import com.example.others.pasteFromClipboard
import com.example.others.showSoftKeyboard


class HomeFragment : Fragment() {
    private lateinit var notificationManager: NotificationManager

    private lateinit var mContext: Context
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView
    private lateinit var prayerView: EditText
    private lateinit var rhemaView: EditText

    private val adapterList = mutableListOf<String>()
    private var prayer = "" // 數字
    private var rhema = "google.com" // 網址
    private var david = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationManager = (activity as MainActivity).getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(!notificationManager.isNotificationPolicyAccessGranted) {
            Toast.makeText((activity as MainActivity), "賦予Prayer勿擾權限後,\n請關閉此設定頁面並重新開啟Prayer", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){}.launch(intent)
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mContext = activity as MainActivity

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

        prayerView = view.findViewById(R.id.prayer)
        prayerView.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                prayer = s.toString()
                generateRhema()
            }
        })
        prayerView.setOnLongClickListener {
            if(prayerView.text.toString() == "") { //帖上
                prayerView.setText(pasteFromClipboard(mContext))
                hideSoftKeyboard(mContext, prayerView.windowToken)
                generateRhema()
                prayerView.clearFocus()
            }
            else { //清空
                prayerView.hint = ""
                prayerView.setText("")
                showSoftKeyboard(mContext, prayerView)
            }
            return@setOnLongClickListener true
        }
        prayerView.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                prayerView.hint = ""
            }
        }

        val dnd = view.findViewById<ImageView>(R.id.dnd)
        val originImageDrawable = dnd.drawable
        itLog(notificationManager.currentInterruptionFilter)
        if(notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE) {
            dnd.setImageResource(R.drawable.dnd_on)
        }
        dnd.setOnClickListener {
            if(notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL) { // turn on
                dnd.setImageResource(R.drawable.dnd_on)
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            }
            else { // turn off
                dnd.setImageDrawable(originImageDrawable)
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }

        adapterList.clear()
        adapterList.addAll(arrayOf("AUTO","nhentai.net","nhentai.com","nhentai.to","Pixiv"))
        listView = view.findViewById(R.id.list_view)
        adapter = object: ArrayAdapter<String>(mContext, R.layout.list_layout, adapterList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var listItemView = convertView
                if(convertView == null) {
                    listItemView = LayoutInflater.from(mContext).inflate(R.layout.list_layout, parent, false)
                }

                val radioButton = listItemView!!.findViewById<RadioButton>(R.id.radio_button)
                radioButton.text = getItem(position)

                radioButton.isChecked = (position == david)
                radioButton.setOnClickListener {
                    david = position
                    notifyDataSetChanged()
                    generateRhema()
                }

                listItemView.setOnClickListener {
                    radioButton.isChecked = (position == david)
                    david = position
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
            copyToClipboard(mContext, rhema)
        }
        view.findViewById<View>(R.id.view).setOnClickListener {
            if(prayerView.text.toString() == "") {
                prayerView.hint = getString(R.string.app_name)
            }
            prayerView.clearFocus()
            rhemaView.clearFocus()
            hideSoftKeyboard(mContext, prayerView.windowToken)
            hideSoftKeyboard(mContext, rhemaView.windowToken)
        }
    }

    private fun generateRhema() {
        when(david) {
            0 -> when (prayer.length) {
                    6 -> {
                        rhema = getString(R.string.nhentai_net,prayer)
                    }
                    8, 9, 10 -> {
                        rhema = getString(R.string.pixiv, prayer)
                    }
                    else -> {
                        rhema = getString(R.string.google)
                    }
                }
            1 -> rhema =  getString(R.string.nhentai_net,prayer)
            2 -> rhema =  getString(R.string.nhentai_com,prayer)
            3 -> rhema = getString(R.string.nhentai_to,prayer)
            4 -> rhema = getString(R.string.pixiv, prayer)
        }
        rhemaView.setText(rhema)
    }
}