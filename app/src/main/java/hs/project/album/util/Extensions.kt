package hs.project.album.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

fun Context.isNetworkConnected(): Boolean {
    var result = false
    val cm = getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                result = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                result = true
            }
        }
    } else {  // API 23보다 아래 기기
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                result = true
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                result = true
            }
        }
    }
    return result
}