package hs.project.album.view.album

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import hs.project.album.BaseFragment
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.databinding.FragmentAlbumView02Binding
import hs.project.album.dialog.CreateAlbumDialog
import hs.project.album.util.*
import hs.project.album.viewmodel.UserAlbumVM

class AlbumView02Frag : BaseFragment<FragmentAlbumView02Binding>(R.layout.fragment_album_view_02),
    View.OnClickListener {

    private var albumList: MutableList<String> = ArrayList()
    private lateinit var userAlbumVM: UserAlbumVM
    private var noneAlbum = false  // 앨범 없는 사용자
    private var hasAlbumNoneImg = false // 앨범은 있고 이미지는 등록 전 사용자
    private var hasImg = false // 앨범, 이미지 등록 사용자

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

        /**
         *
         * ViewModel LifeData 로 실시간 데이터 변경 감지
         *
         * user 가 가지고 있는 앨범 idx 값을 가지고
         * 앨범의 idx 값들을 비교해야됨
         * 1. user 데이터 의 album_list 가지고오기
         * 2. for 문으로 존재하는 문서인지 검사
         * 3. 앨범에 image_list 값 있는지 검사
         */

        userAlbumVM.vmAlbumList.observe(viewLifecycleOwner, { list ->

            (parentFragment as? AlbumFrag)?.visibleLoading()

            if (activity != null && isAdded) {
                if (requireActivity().isNetworkConnected()) {
                    list?.let { it -> checkMyAlbum(it) }
                } else {
                    requireActivity().displayToast(requireActivity().resString(R.string.str_network_fail))
                }
            }
        })
    }

    fun init() {
        binding.noneAlbum.setOnClickListener(this)
        getUserAlbumList()
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
                    noneAlbum = false
                    hasAlbumNoneImg = false
                    hasImg = false

                    albumList = document["album_list"] as ArrayList<String>

                    if (albumList.size > 0 || albumList.isNotEmpty()) {
                        hasAlbumNoneImg = true

                        /**
                         * 앨범,이미지 전부 있을 때 비교할 값 설정 필요
                         */
                        /*if (){

                        }*/

                    } else {
                        noneAlbum = true
                    }

                    mainViewStatus(noneAlbum, hasAlbumNoneImg, hasImg)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("hs", "get failed with ", exception)
            }
    }

    private fun checkMyAlbum(albumList: MutableList<String>) {

        var imageList: MutableList<String> = ArrayList()

        noneAlbum = false
        hasAlbumNoneImg = false
        hasImg = false

        // 가입되어 있는 앨범이 있는지 검사
        for (item in albumList) {
            MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.ALBUM_LIST)
                .document(item)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {  // 만들었거나 초대받은 앨범이 존재

                        imageList = document["image_list"] as MutableList<String>

                        if (imageList.size > 1) {  // 이미지가 있을 때
                            hasImg = true
                        } else {  // 이미지가 없을 때
                            hasAlbumNoneImg = true
                        }

                    } else {  // 앨범 존재하지 않음
                        noneAlbum = true
                    }
                    mainViewStatus(noneAlbum, hasAlbumNoneImg, hasImg)
                }
                .addOnFailureListener { exception ->
                    Log.d("hs", "get failed with ", exception)
                }
        }

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