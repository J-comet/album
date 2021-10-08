package hs.project.album.view.album

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import hs.project.album.BaseFragment
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.data.AddPhotoData
import hs.project.album.databinding.FragmentAlbumView02Binding
import hs.project.album.dialog.CommonDialog
import hs.project.album.dialog.CreateAlbumDialog
import hs.project.album.util.*
import hs.project.album.viewmodel.UserAlbumVM

class AlbumView02Frag : BaseFragment<FragmentAlbumView02Binding>(R.layout.fragment_album_view_02),
    View.OnClickListener {

    private var albumList: ArrayList<String> = ArrayList()
    private lateinit var userAlbumVM: UserAlbumVM

    companion object {
        const val TAG = "AlbumViewPager02"
        fun newInstance(): AlbumView02Frag {
            return AlbumView02Frag()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAlbumVM = ViewModelProvider(requireActivity()).get(UserAlbumVM::class.java)
        init()

        // 현재 앨범리스트에 변화가 없으므로 이미지를 추가했을 때 MainView 상태가 변화가 없음
        /**
         * 해결방안
         * 이미지리스트에 변화가 있을 때 .observe 하기
         * 앨범리스트에 다른 값을 추가해서 이미지 리스트에 변화가 있음을 감지하도록 설정 하기
         */
        userAlbumVM.vmAlbumList.observe(viewLifecycleOwner, { list ->

            (parentFragment as? AlbumFrag)?.visibleLoading()

            if (activity != null && isAdded) {
                if (requireActivity().isNetworkConnected()) {
                    list?.let { checkMyAlbum() }
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
            }
        })

        userAlbumVM.vmImgList.observe(viewLifecycleOwner, { list ->
            (parentFragment as? AlbumFrag)?.visibleLoading()

            if (activity != null && isAdded) {
                if (requireActivity().isNetworkConnected()) {
                    list?.let { checkMyAlbum() }
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
            }
        })

    }

    fun init() {
        binding.noneAlbum.setOnClickListener(this)

        // 서버에 저장되어 있는 앨범 중 현재 preference 에 저장되어 있는 앨범의
        // imageList 에 이미지 없을 때 getUserAlbumList() 함수 실행

        var imgList: ArrayList<String> = ArrayList()

        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.ALBUM_LIST)
        .document(MyApplication.prefs.getString(Constant.PREFERENCE_KEY.USE_ALBUM_ID, "none"))
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    imgList = document["image_list"] as ArrayList<String>

                    if (imgList.size > 0 && imgList.isNotEmpty()) {
                        mainViewStatus(noneAlbum = false, hasAlbumNoneImg = false, hasImg = true)
                    } else {
                        getUserAlbumList()
                    }
                }
                else {
                    mainViewStatus(noneAlbum = true, hasAlbumNoneImg = false, hasImg = false)
                }
            }

    }

    private fun mainViewStatus(noneAlbum: Boolean, hasAlbumNoneImg: Boolean, hasImg: Boolean) {
        when (true) {
            noneAlbum -> {
                if (activity != null && isAdded) {
                    requireActivity().runOnUiThread {
                        binding.root.post {
                            binding.noneAlbum.visible()
                            binding.noneImage.hide(1)
                            binding.useAlbumImg.hide(1)
                        }
                    }
                }
            }
            hasAlbumNoneImg -> {
                if (activity != null && isAdded) {
                    requireActivity().runOnUiThread {
                        binding.root.post {
                            binding.noneAlbum.hide(1)
                            binding.noneImage.visible()
                            binding.useAlbumImg.hide(1)
                        }
                    }
                }
            }
            hasImg -> {
                if (activity != null && isAdded) {
                    requireActivity().runOnUiThread {
                        binding.root.post {
                            binding.noneAlbum.hide(1)
                            binding.noneImage.hide(1)
                            binding.useAlbumImg.visible()
                        }
                    }
                }
            }
            else -> {
                if (activity != null && isAdded) {
                    requireActivity().runOnUiThread {
                        binding.root.post {
                            binding.noneAlbum.visible()
                            binding.noneImage.hide(1)
                            binding.useAlbumImg.hide(1)
                        }
                    }
                }
            }
        }
    }

    private fun getUserAlbumList() {
        // user 데이터의 album Idx List 가져옴
        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.USER_LIST)
            .document(MyApplication.firebaseAuth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    albumList = document["album_list"] as ArrayList<String>

                    if (albumList.size > 0 && albumList.isNotEmpty()) {
                        mainViewStatus(noneAlbum = false, hasAlbumNoneImg = true, hasImg = false)
                    }

                } else {
                    mainViewStatus(noneAlbum = true, hasAlbumNoneImg = false, hasImg = false)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("hs", "get failed with ", exception)
            }
    }

    private fun checkMyAlbum() {

        var imageList: ArrayList<String> = ArrayList()

        // 가입되어 있는 앨범이 있는지 검사
//        for (item in albumList) {
            MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.ALBUM_LIST)
                .document(MyApplication.prefs.getString(Constant.PREFERENCE_KEY.USE_ALBUM_ID,"none"))
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {  // 만들었거나 초대받은 앨범이 존재

                        imageList = document["image_list"] as ArrayList<String>

                        if (imageList.size > 0) {  // 이미지가 있을 때
                            mainViewStatus(noneAlbum = false, hasAlbumNoneImg = false, hasImg = true)

                        } else {  // 이미지가 없을 때
                            mainViewStatus(noneAlbum = false, hasAlbumNoneImg = true, hasImg = false)
                        }

                    } else {  // 앨범 존재하지 않음
                        mainViewStatus(noneAlbum = true, hasAlbumNoneImg = false, hasImg = false)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.d("hs", "get failed with ", exception)
                }
//        }

        (parentFragment as? AlbumFrag)?.hideLoading()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.noneAlbum.id -> {
                CreateAlbumDialog().show(
                    childFragmentManager,
                    "CreateAlbumDialog"
                )
            }
        }
    }

}