package com.example.others

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import com.example.prayer.MainActivity

fun pasteFromClipboard(mainActivity: MainActivity): String {
    val clipboard = mainActivity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    if(clipboard.primaryClip == null) {
        return ""
    }
    else {
        return clipboard.primaryClip!!.getItemAt(0).text.toString()
    }
}

fun copyToClipboard(mainActivity: MainActivity, prayer: String) {
    val clipboard = mainActivity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(null, prayer)
    clipboard.setPrimaryClip(clipData)
    Toast.makeText(mainActivity, "Copied", Toast.LENGTH_SHORT).show()
}