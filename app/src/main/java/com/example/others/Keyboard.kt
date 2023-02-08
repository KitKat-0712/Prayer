package com.example.others

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun hideSoftKeyboard(context: Context, windowToken: IBinder) {
    val imm: InputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun showSoftKeyboard(context: Context, editText: EditText) {
    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(editText, 0)
}