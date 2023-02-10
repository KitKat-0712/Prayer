package com.example.prayer

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

const val defaultWebsite = "github.com"
const val filesDir = "/data/user/0/com.example.prayer/files"

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

    override fun onStop() {
        super.onStop()
        notificationManager.setInterruptionFilter(originInterruptionFilter)
        finish()
    }
    fun restart() {
        finish()
        startActivity(intent)
    }
    override fun onBackPressed() {
        replaceNowFragmentWith(HomeFragment())
    }
}