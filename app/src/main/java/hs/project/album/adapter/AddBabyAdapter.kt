package hs.project.album.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hs.project.album.R
import hs.project.album.data.AddBabyData
import hs.project.album.databinding.ItemAddBabyBinding

class AddBabyAdapter(val context: Context, private var list: MutableList<AddBabyData>) :
    RecyclerView.Adapter<AddBabyAdapter.AddBabyHolder>() {

    private var selectedPosition = 1
    private var mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddBabyHolder {
        val itemBinding =
            ItemAddBabyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddBabyHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: AddBabyHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun setData(dataList: MutableList<AddBabyData>) {
        list = dataList
        notifyDataSetChanged()
    }

    private fun removeData(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

//    fun clearData() {
//        list.clear()
//        notifyDataSetChanged()
//    }

    inner class AddBabyHolder(val binding: ItemAddBabyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AddBabyData) {
            binding.addBabyData = item
            binding.btnDelete.setOnClickListener {
                selectedPosition = adapterPosition

                val builder = AlertDialog.Builder(mContext)
                    .setMessage("정말 삭제하시겠습니까")
                    .setCancelable(true)
                    .setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                        removeData(
                            selectedPosition
                        )
                    }
                    .setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int -> }.show()
                builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(mContext, R.color.color_808080))
                builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(mContext, R.color.black))
            }
        }
    }

}