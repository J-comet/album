package hs.project.album.view.setting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import hs.project.album.BaseFragment
import hs.project.album.R
import hs.project.album.databinding.FragmentFamilyBinding
import hs.project.album.databinding.FragmentSettingBinding
import hs.project.album.view.MainActivity

class SettingFrag : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    companion object {
        const val TAG : String = "가족설정 프래그먼트"
        fun newInstance(): SettingFrag {
            return SettingFrag()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

    }

}