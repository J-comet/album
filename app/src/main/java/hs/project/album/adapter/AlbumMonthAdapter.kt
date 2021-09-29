package hs.project.album.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import hs.project.album.R
import hs.project.album.data.AlbumMonth
import hs.project.album.databinding.ItemAlbumMenuBinding
import hs.project.album.view.album.AlbumView01Frag
import hs.project.album.view.album.AlbumView02Frag


class AlbumMonthAdapter(
    private val list: List<AlbumMonth>,
    private val manager: FragmentManager
) : RecyclerView.Adapter<AlbumMonthAdapter.MonthHolder>() {

    private var selectedPosition = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthHolder {
        val itemBinding =
            ItemAlbumMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonthHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MonthHolder, position: Int) {
        holder.bind(list[position])

        if (position == 0) {  // collection 메뉴
            holder.binding.itemCollection.visibility = View.VISIBLE
            holder.binding.itemText.visibility = View.GONE
        } else {  // Text 메뉴 = '월'
            holder.binding.itemText.visibility = View.VISIBLE
            holder.binding.itemCollection.visibility = View.GONE
        }

        if (position == selectedPosition) {

            holder.binding.tvItemMonth.setTextColor(Color.parseColor("#000000"))
            holder.binding.viewLine.visibility = View.VISIBLE

            manager.beginTransaction().replace(
                R.id.flayout_album_container,
                AlbumView02Frag.newInstance()
            ).commit()


            if (position == 0) {
                holder.binding.ivItemCollection.setColorFilter(
                    Color.parseColor("#000000"),
                    PorterDuff.Mode.SRC_IN
                )

                manager.beginTransaction().replace(
                    R.id.flayout_album_container,
                    AlbumView01Frag.newInstance()
                ).commit()
            }

        } else {
            holder.binding.ivItemCollection.setColorFilter(
                Color.parseColor("#E2E2E2"),
                PorterDuff.Mode.SRC_IN
            )
            holder.binding.tvItemMonth.setTextColor(Color.parseColor("#E2E2E2"))
            holder.binding.viewLine.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MonthHolder(val binding: ItemAlbumMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AlbumMonth) {
            binding.monthItem = item
            binding.menuItem.setOnClickListener {
                selectedPosition = adapterPosition

                /**
                 * AlbumView02Frag 로 현재 몇월달인지 데이터 보내주는 코드 추가 필요
                 */
                /*val bundle = Bundle()
                bundle.putSerializable("LASTNAME", lastNameModel)
                val intent = Intent(it.context, LastNameFragment::class.java)
                intent.putExtra("data", bundle)
                mContext.startActivity(intent)*/


                notifyDataSetChanged()
            }
        }
    }

}