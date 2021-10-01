package hs.project.album.util

import android.content.Context
import android.view.View
import android.widget.Toast

fun Context.displayToast(message: String?){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun View.visible(){
    this.visibility = View.VISIBLE
}

fun View.hide(state : Int){
    when(state){
        0 -> this.visibility = View.INVISIBLE
        1 -> this.visibility = View.GONE
        else -> this.visibility = View.GONE
    }

}