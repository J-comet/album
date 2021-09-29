package hs.project.album.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hs.project.album.databinding.ItemSlideImgBinding


class SlideViewPagerAdapter(slideImgList: ArrayList<Int>) :
    RecyclerView.Adapter<SlideViewPagerAdapter.PagerViewHolder>() {
    var item = slideImgList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val itemBinding =
            ItemSlideImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(item[position])
    }

    override fun getItemCount(): Int = item.size

    inner class PagerViewHolder(val binding: ItemSlideImgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Int) {
            binding.imgRes = item
        }
    }




}