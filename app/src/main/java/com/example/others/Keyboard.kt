package com.example.others

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.prayer.MainActivity

fun hideSoftKeyboard(mainActivity: MainActivity, windowToken: IBinder) {
    val imm: InputMethodManager = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun showSoftKeyboard(mainActivity: MainActivity, editText: EditText) {
    val inputMethodManager = mainActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(editText, 0)
}