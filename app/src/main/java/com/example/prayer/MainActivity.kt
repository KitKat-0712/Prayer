package com.example.prayer

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.File

const val defaultWebsite = "github.com"
var isHomeFragment = true
var isWebFragment = false

class MainActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private var originInterruptionFilter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        originInterruptionFilter = notificationManager.currentInterruptionFilter
        setContentView(R.layout.activity_main)
        addFragments(HomeFragment(), WebFragment(defaultWebsite), BookmarksLibraryFragment())
    }

    private fun addFragments(vararg fragments: Fragment) {
        for(fragment in fragments) {
            val transAction = supportFragmentManager.beginTransaction()
            transAction.add(R.id.fragment_container, fragment)
            transAction.commit()
        }
    }
    fun replaceNowFragmentWith(fragment: Fragment) {
        val transAction = supportFragmentManager.beginTransaction()
        transAction.replace(R.id.fragment_container, fragment)
        transAction.commit()
    }

    override fun onPause() {
        notificationManager.setInterruptionFilter(originInterruptionFilter)
        if(isWebFragment) {
            isHomeFragment = true
            isWebFragment = false
            finish()
        }
        super.onPause()
    }
    fun restart() {
        finish()
        startActivity(intent)
    }
    override fun onBackPressed() {
        if(isHomeFragment) {
            super.onBackPressed()
        }
        else {
            isHomeFragment = true
            isWebFragment = false
            replaceNowFragmentWith(HomeFragment())
        }
    }

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
}