package hs.project.album.view.album

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hs.project.album.*
import hs.project.album.adapter.AlbumMonthAdapter
import hs.project.album.data.AddPhotoData
import hs.project.album.data.AlbumMonth
import hs.project.album.databinding.FragmentAlbumBinding
import hs.project.album.dialog.CommonDialog
import hs.project.album.util.*
import hs.project.album.viewmodel.UserAlbumVM
import java.util.*
import kotlin.collections.ArrayList


class AlbumFrag : BaseFragment<FragmentAlbumBinding>(R.layout.fragment_album) {

    private val monthList: MutableList<AlbumMonth> = ArrayList()
    private var month: Int = 0
    private lateinit var userAlbumVM: UserAlbumVM

    companion object {
        const val TAG: String = "앨범 프래그먼트"
        fun newInstance(): AlbumFrag {
            return AlbumFrag()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAlbumVM = ViewModelProvider(requireActivity()).get(UserAlbumVM::class.java)

        if (activity != null && isAdded) {
            if (!requireActivity().isNetworkConnected()) {
                val dialog = CommonDialog(requireActivity().resString(R.string.str_network_fail))
                dialog.show(childFragmentManager, "CommonDialog")
                dialog.setOnClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun onClicked() {
                        dialog.dismiss()
                    }
                })
            }
        }

        setView02Fragment()

        init()
        initRecyclerView()
    }

    private fun init() {
        binding.tvYear.text = getCurYear()
        month = getCurMonth().toInt()
    }

    fun setView02Fragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.flayout_album_container,
            AlbumView02Frag.newInstance()
        )
            .addToBackStack(null)
            .commit()
    }

    private fun initRecyclerView() {

        val manager: RecyclerView.LayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        /**
         * [1] 처음 앱을 사용하는 유저인지 확인 필요
         * 현재 '월'이 몇월인지 확인 후 해당월만 recyclerview 에 추가
         */

        for (item in month + 1 downTo 1) {
            monthList.add(AlbumMonth("$item" + "월"))
        }

        /**
         * [2] 기존사용자 or 초대 받은 사용자 일 때는 다른 로직 추가 필요
         * 기존데이터를 모두 불러와서 recyclerview 에 추가
         */

        binding.rvAlbumMenu.apply {
            adapter = AlbumMonthAdapter(monthList, childFragmentManager)
            layoutManager = manager
            scrollToPosition(0)
        }
    }

    fun visibleLoading() {
        binding.loadingView.visible()
    }

    fun hideLoading() {
        binding.loadingView.hide(0)
    }

//    override fun onClick(v: View?) {
//        when (v!!.id) {
//            R.id.iv -> finish()
//            R.id.llAgree -> {
//                val dialogAgree = DialogAgree(this, R.layout.dialog_agree, "이용 약관 동의")
//                dialogAgree.show()
//            }
//        }
//    }

}