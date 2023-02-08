package com.example.prayer

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private var originInterruptionFilter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        originInterruptionFilter = notificationManager.currentInterruptionFilter
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_main)
        addFragments(HomeFragment(), WebFragment())
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
}