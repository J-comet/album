package hs.project.album.view.add


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import hs.project.album.R
import hs.project.album.databinding.DialogAddImageBinding
import hs.project.album.view.MainActivity


class AddImageDialog(selectedImgUri: Uri) : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogAddImageBinding
    private var saveType = "none"
    private val selectedUri = selectedImgUri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
        isCancelable = true // 화면밖 or 뒤로가기버튼으로 dialog dismiss = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        binding.btnAll.setOnClickListener(this)
        binding.btnLimit.setOnClickListener(this)
        binding.ivImage.setOnClickListener(this)
        binding.clayoutBtnRegister.setOnClickListener(this)

        //  최초 선택상태
        selectedStatus(
            selectedAll = true,
            selectedLimit = false
        )
        saveType = binding.tvAll.text.toString()

        Glide.with(this)
            .load(selectedUri)
            .override(850,850)
            .transform(CenterCrop(),RoundedCorners(9))
            .into(binding.ivImage)


        /**
         * 비율 맞춰서 출력하는 법 찾기
         */
    }

    private fun selectedStatus(
        selectedAll: Boolean,
        selectedLimit: Boolean
    ) {
        binding.btnAll.isSelected = selectedAll
        binding.tvAll.isSelected = selectedAll

        binding.btnLimit.isSelected = selectedLimit
        binding.tvLimit.isSelected = selectedLimit
    }

    override fun onClick(v: View?) {

        when(v?.id){
            binding.btnAll.id -> {
                selectedStatus(selectedAll = true, selectedLimit = false)
                saveType = binding.tvAll.text.toString()
            }
            binding.btnLimit.id -> {
                selectedStatus(selectedAll = false, selectedLimit = true)
                saveType = binding.tvLimit.text.toString()
            }
            binding.ivImage.id -> {
                dismiss()
                (activity as MainActivity).openGallery()
            }
            binding.clayoutBtnRegister.id -> {
                /**
                 * 서버에 저장해야될 데이터
                 * 'saveType' , image , userIdx , image , date
                 */
            }

        }
    }
}