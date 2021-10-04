package hs.project.album.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import hs.project.album.R
import hs.project.album.databinding.DialogCommonBinding


class CommonDialog(msg: String) : DialogFragment() {
    private lateinit var onClickListener: OnDialogClickListener
    private lateinit var binding: DialogCommonBinding

    private var tvMsg = msg

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false // 화면밖 or 뒤로가기버튼으로 dialog dismiss = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCommonBinding.inflate(layoutInflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.tvMsg.text = tvMsg
        binding.btnOk.setOnClickListener {
            dismiss()
            onClickListener.onClicked()
        }
    }

    interface OnDialogClickListener {
        fun onClicked()
    }

}