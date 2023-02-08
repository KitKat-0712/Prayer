package com.example.custom

import android.content.Context
import android.util.Log
import android.widget.Toast

const val tag = "NachoNeko"

fun itLog() {
    Log.d(tag,"\n")
}
fun <T> itLog(vararg msg: T) {
    var cat = ""
    for (i in msg) {
        cat += "$i "
    }
    cat = cat.substring(0, cat.lastIndex)
    Log.d(tag, cat)
}
fun itLog(msg: String, variable: Any?) {
    itLog()
    itLog("- $msg -")
    itLog(variable)
}
fun itToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}