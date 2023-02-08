package com.example.others

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast

fun pasteFromClipboard(context: Context): String {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    if(clipboard.primaryClip == null) {
        return ""
    }
    else {
        return clipboard.primaryClip!!.getItemAt(0).text.toString()
    }
}

fun copyToClipboard(context: Context, prayer: String) {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(null, prayer)
    clipboard.setPrimaryClip(clipData)
    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
}