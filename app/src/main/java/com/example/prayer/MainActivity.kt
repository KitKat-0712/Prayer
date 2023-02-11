package com.example.prayer

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileNotFoundException

val defaultConfig = """
        enable_secure_flag=1
        enable_override_onPause=1
    """.trimIndent()
const val defaultWebsite = "github.com"
var isNowHomeFragment = true
var isNowWebFragment = false

class MainActivity: AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private var originInterruptionFilter = 0
    private var enableOverrideOnPause = true
    private var enableSecureFlag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doConfig(reset=false, restart=false)
        if(enableSecureFlag) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

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

        isNowHomeFragment = false
        if(fragment is HomeFragment) {
            isNowHomeFragment = true
            isNowWebFragment = false
        }

        if(fragment is WebFragment) {
            isNowWebFragment = true
            isNowHomeFragment = false
        }
    }

    override fun onPause() {
        notificationManager.setInterruptionFilter(originInterruptionFilter)
        if(enableOverrideOnPause) {
            if(isNowWebFragment) {
                replaceNowFragmentWith(HomeFragment())
            }
        }
        super.onPause()
    }
    fun restart() {
        finish()
        startActivity(intent)
    }
    override fun onBackPressed() {
        if(isNowHomeFragment) {
            super.onBackPressed()
        }
        else {
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

    fun doConfig(reset: Boolean, restart: Boolean) {
        if(reset) {
            File(filesDir, "config.txt").writeText(defaultConfig)
            Toast.makeText(this, "config.txt has been reset", Toast.LENGTH_SHORT).show()
        }
        else {
            var noError = true
            try {
                val string = File(filesDir, "config.txt").readText().removeSuffix("\r")
                var configNumber = 0
                val list = string.split('\n')
                for(i in list) {
                    val tempList = i.split('=')
                    val option = tempList[0]
                    val choice = tempList[1]
                    when(option) {
                        "enable_secure_flag" -> {
                            enableSecureFlag = when(choice) {
                                "0" -> false
                                "1" -> true
                                else -> throw Exception()
                            }
                            configNumber++
                        }

                        "enable_override_onPause" -> {
                            enableOverrideOnPause = when(choice) {
                                "0" -> false
                                "1" -> true
                                else -> throw Exception()
                            }
                            configNumber++
                        }

                        else -> throw Exception()
                    }
                }

                if(configNumber != 2) {
                    throw Exception()
                }
            }
            catch(e: Exception) {
                File(filesDir, "config.txt").writeText(defaultConfig)
                enableOverrideOnPause = true
                noError = false
                if(e !is FileNotFoundException) {
                    Toast.makeText(this, "Due to config.txt format error\nIt has been reset", Toast.LENGTH_SHORT).show()
                }
            }
            if(noError) {
                if(restart) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if(restart) {
            restart()
        }
    }
}