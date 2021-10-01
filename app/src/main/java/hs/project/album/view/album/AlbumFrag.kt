package hs.project.album.view.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hs.project.album.BaseActivity
import hs.project.album.BaseFragment
import hs.project.album.R
import hs.project.album.adapter.AlbumMonthAdapter
import hs.project.album.data.AlbumMonth
import hs.project.album.databinding.ActivityMainBinding
import hs.project.album.databinding.FragmentAlbumBinding
import hs.project.album.util.getCurMonth
import hs.project.album.util.getCurYear
import hs.project.album.util.hide
import hs.project.album.util.visible
import hs.project.album.view.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class AlbumFrag : BaseFragment<FragmentAlbumBinding>(R.layout.fragment_album) {

    private val monthList: MutableList<AlbumMonth> = ArrayList()
    private var month: Int = 0

    companion object {
        const val TAG: String = "앨범 프래그먼트"
        fun newInstance(): AlbumFrag {
            return AlbumFrag()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction().replace(
            R.id.flayout_album_container,
            AlbumView02Frag.newInstance()
        )
            .addToBackStack(null)
            .commit()
        init()
        initRecyclerView()
    }

    private fun init() {
        binding.tvYear.text = getCurYear()
        month = getCurMonth().toInt()
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