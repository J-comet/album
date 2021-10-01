package hs.project.album.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hs.project.album.R
import hs.project.album.adapter.AddBabyAdapter
import hs.project.album.data.AddBabyData
import hs.project.album.databinding.DialogCreateAlbumBinding
import hs.project.album.util.hide
import hs.project.album.util.visible
import hs.project.album.viewmodel.AddBabyVM
import java.util.*


class CreateAlbumDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogCreateAlbumBinding
    private var spinnerResult = ""
    private lateinit var model: AddBabyVM
    private lateinit var getList: MutableList<AddBabyData>

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

        model.vmAddBabyData.observe(viewLifecycleOwner, { addBabyData ->
//            val name = addBabyData?.name
//            val gender = addBabyData?.gender
//            val birthday = addBabyData?.birthday

            addBabyData?.let { getList.add(it) }
            if (binding.clayoutNoneContent.visibility == View.VISIBLE) {
                binding.clayoutNoneContent.hide(1)
                binding.rvBabyRegister.visible()
            }
            initRecyclerView(getList)

            (binding.rvBabyRegister.adapter as AddBabyAdapter).setData(getList)
        })

        init()

    }

    private fun init() {
        binding.btnBack.setOnClickListener(this)
        binding.btnAddBaby.setOnClickListener(this)
        binding.clayoutBtnRegister.setOnClickListener(this)

        setSpinnerView()
        getList = mutableListOf()

        initRecyclerView(getList)

        if (getList.size > 1) {
            requireActivity().runOnUiThread {
                binding.root.post {
                    binding.rvBabyRegister.visible()
                    binding.clayoutNoneContent.hide(1)
                }
            }
        } else {
            requireActivity().runOnUiThread {
                binding.root.post {
                    binding.rvBabyRegister.hide(1)
                    binding.clayoutNoneContent.visible()
                }
            }
        }

    }

    private fun initRecyclerView(addList: MutableList<AddBabyData>) {

        val manager: RecyclerView.LayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.rvBabyRegister.apply {
            adapter = AddBabyAdapter(requireActivity(),addList)
            layoutManager = manager
            addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    LinearLayoutManager.VERTICAL
                )
            )
        }

    }

    private fun setSpinnerView() {
        val spinnerItems = requireActivity().resources?.getStringArray(R.array.arr_family)
        val spinnerAdapter = ArrayAdapter(requireActivity(), R.layout.item_spinner, spinnerItems!!)

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

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        // 뒤로가기 버튼 클릭시 할 동작 재정의
        binding.btnBack.performClick()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnBack.id -> {
                model.clear()
                dismiss()
            }
            binding.btnAddBaby.id -> {
                AddBabyDialog().show(childFragmentManager, "AddBabyDialog")
            }
            binding.clayoutBtnRegister.id -> {

            }
        }
    }

}
