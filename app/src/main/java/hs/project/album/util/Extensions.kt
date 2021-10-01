package hs.project.album.util

import android.content.Context
import android.view.View
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun Context.displayToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hide(state: Int) {
    when (state) {
        0 -> this.visibility = View.INVISIBLE
        1 -> this.visibility = View.GONE
        else -> this.visibility = View.GONE
    }
}

fun Context.resString(strId: Int): String {
    return resources.getString(strId)
}

fun getCurYear(): String {
    val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    return yearFormat.format(Calendar.getInstance().time)
}

fun getCurMonth(): String {
    val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
    return monthFormat.format(Calendar.getInstance().time)
}

fun getTodayDate(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.time
}