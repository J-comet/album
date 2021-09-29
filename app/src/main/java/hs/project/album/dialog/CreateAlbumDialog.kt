package hs.project.album.dialog


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import hs.project.album.R
import hs.project.album.databinding.DialogCreateAlbumBinding
import hs.project.album.viewmodel.AddBabyVM


class CreateAlbumDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogCreateAlbumBinding
    private var spinnerResult = ""
    private var babyName = ""
    private var babyGender = ""
    private var babyBirthday = ""
    private lateinit var model: AddBabyVM

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
        binding = DialogCreateAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(AddBabyVM::class.java)

        /**
         * recyclerview 아이템 추가할 때 사용할 데이터
         */

        model.birthday.observe(viewLifecycleOwner, Observer {
            Log.e("생일",it)
        })
        model.name.observe(viewLifecycleOwner, Observer {
            Log.e("이름",it)
        })
        model.gender.observe(viewLifecycleOwner, Observer {
            Log.e("성별",it)
        })
        init()
    }

    private fun init() {
        binding.btnBack.setOnClickListener(this)
        binding.btnAddBaby.setOnClickListener(this)
        binding.clayoutBtnRegister.setOnClickListener(this)

        setSpinnerView()
    }

    private fun setSpinnerView(){
        val spinnerItems = requireActivity().resources?.getStringArray(R.array.arr_family)
        val spinnerAdapter = ArrayAdapter(requireActivity(),R.layout.item_spinner, spinnerItems!!)

        binding.spinnerView.adapter = spinnerAdapter
        binding.spinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerResult = spinnerItems[position].toString()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.btnBack.id -> {
                dismiss()
            }
            binding.btnAddBaby.id -> {
                AddBabyDialog().show(childFragmentManager,"AddBabyDialog")
            }
            binding.clayoutBtnRegister.id -> {

            }
        }
    }

}
