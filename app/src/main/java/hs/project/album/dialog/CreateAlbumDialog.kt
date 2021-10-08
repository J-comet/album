package hs.project.album.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.adapter.AddBabyAdapter
import hs.project.album.data.AddBabyData
import hs.project.album.data.AddPhotoData
import hs.project.album.data.CreateAlbum
import hs.project.album.databinding.DialogCreateAlbumBinding
import hs.project.album.util.*
import hs.project.album.view.album.AlbumFrag
import hs.project.album.viewmodel.AddBabyVM
import hs.project.album.viewmodel.UserAlbumVM


class CreateAlbumDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogCreateAlbumBinding
    private var spinnerResult = ""
    private lateinit var addBabyVM: AddBabyVM
    private lateinit var userAlbumVM: UserAlbumVM
    private lateinit var babyList: ArrayList<AddBabyData>
    private var albumList: ArrayList<String> = ArrayList()
    private var isName = false
    private var isRelation = false

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
        addBabyVM = ViewModelProvider(requireActivity()).get(AddBabyVM::class.java)
        userAlbumVM = ViewModelProvider(requireActivity()).get(UserAlbumVM::class.java)

        addBabyVM.vmAddBabyData.observe(viewLifecycleOwner, { addBabyData ->
//            val name = addBabyData?.name
//            val gender = addBabyData?.gender
//            val birthday = addBabyData?.birthday

            addBabyData?.let { babyList.add(it) }
            if (binding.clayoutNoneContent.visibility == View.VISIBLE) {
                binding.clayoutNoneContent.hide(1)
                binding.rvBabyRegister.visible()
            }
            initRecyclerView(babyList)

            (binding.rvBabyRegister.adapter as AddBabyAdapter).setData(babyList)
        })

        init()

    }

    private fun init() {
        binding.btnBack.setOnClickListener(this)
        binding.btnAddBaby.setOnClickListener(this)
        binding.clayoutBtnRegister.setOnClickListener(this)

        setSpinnerView()
        babyList = arrayListOf()

        initRecyclerView(babyList)

        if (babyList.size > 1) {
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

        binding.tilName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name: String = binding.tilName.editText?.text.toString()
                isName = name.isNotEmpty()
            }
        })

    }

    private fun initRecyclerView(addList: MutableList<AddBabyData>) {

        val manager: RecyclerView.LayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.rvBabyRegister.apply {
            adapter = AddBabyAdapter(requireActivity(), addList)
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
                isRelation = spinnerResult.isNotEmpty()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        // 뒤로가기 버튼 클릭시 할 동작 재정의
        binding.btnBack.performClick()
    }

    // 유저데이터에 album_idx 값 추가
    private fun addAlbumIdx(albumIdx: String) {
        val albumList: ArrayList<String> = ArrayList()
        albumList.add(albumIdx)

        val data = hashMapOf("album_list" to albumList)

        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.USER_LIST)
            .document(MyApplication.firebaseAuth.currentUser?.email.toString())
            .set(data, SetOptions.merge())
            .addOnFailureListener { exception ->
                Log.e("CreateAlbumDialog", "get failed with ", exception)
            }

        /**
         * 최초 사용자는 이부분에서 앨범 여부를 확인
         * userAlbumVM 에 데이터 추가
         */
        for (i in albumList.indices) {
            userAlbumVM.addAlbum(albumList[i])
        }
    }

    private fun createAlbum() {
        val createAlbum = CreateAlbum()
        val currentUserUID = MyApplication.firebaseAuth.currentUser?.uid.toString()
        val masterUidList: ArrayList<String> = ArrayList()
        val imageList: ArrayList<AddPhotoData> = ArrayList()
        val joinUserUidList: ArrayList<String> = ArrayList()

        masterUidList.add(currentUserUID)
        joinUserUidList.add(currentUserUID)

        createAlbum.apply {

            val createAlbumInfo = hashMapOf(
                "user_uid" to currentUserUID,
                "album_name" to binding.tilName.editText?.text.toString(),
                "master_uid_list" to masterUidList,
                "image_list" to imageList,
                "user_list" to joinUserUidList,
                "baby_list" to babyList,
                "date" to FieldValue.serverTimestamp()
            )

            // 자동으로 문서 이름 추가할 때
//            db.collection("post")
//                .add(createTeamInfo)
//                .addOnSuccessListener { Log.d("hs", "DocumentSnapshot successfully written!") }
//                .addOnFailureListener { e -> Log.w("hs", "Error writing document", e) }

            // 직접 문서 이름 입력
            MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.ALBUM_LIST)
                .document("album_$currentUserUID")
                .set(createAlbumInfo)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.loadingView.hide(1)
                        requireActivity().displayToast("앨범 생성")

                        /**
                         * 현재 유저가 사용중인 앨범 ID
                         */
                        if (MyApplication.prefs.getString(Constant.PREFERENCE_KEY.USE_ALBUM_ID, "none") == "none") {
                            MyApplication.prefs.setString(
                                Constant.PREFERENCE_KEY.USE_ALBUM_ID,
                                "album_$currentUserUID"
                            )
                        }

                        addAlbumIdx("album_$currentUserUID") // 유저데이터에 앨범 Idx 값 추가
                        dismiss()

                        (parentFragment as? AlbumFrag)?.setView02Fragment()
                    }
                }
                .addOnSuccessListener { Log.d("hs", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("hs", "Error writing document", e) }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnBack.id -> {
                addBabyVM.clear()
                dismiss()
            }
            binding.btnAddBaby.id -> {
                AddBabyDialog().show(childFragmentManager, "AddBabyDialog")
            }
            binding.clayoutBtnRegister.id -> {

                if (activity != null && isAdded) {
                    if (isName && isRelation && babyList.isNotEmpty()) {

                        if (requireActivity().isNetworkConnected()) {
                            binding.loadingView.visible()
                            createAlbum()
                        } else {
                            val dialog =
                                CommonDialog(requireActivity().resString(R.string.str_network_fail))
                            dialog.show(childFragmentManager, "CommonDialog")
                            dialog.setOnClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun onClicked() {
                                    dialog.dismiss()
                                }
                            })
                        }

                    } else {
                        requireActivity().displayToast(requireActivity().resString(R.string.str_please_info))
                    }
                }
            }
        }
    }

}
