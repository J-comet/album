package hs.project.album.adapter.binding_adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

object BindingConversion {

    @BindingAdapter("setImgResource")
    @JvmStatic
    fun setImgResource(v: ImageView, resId:Int){
        Glide.with(v.context)
            .load(resId)
            .transform(CenterCrop(), RoundedCorners(9))
            .into(v)
    }
}