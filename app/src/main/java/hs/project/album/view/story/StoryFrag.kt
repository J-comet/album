package hs.project.album.view.story

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import hs.project.album.BaseFragment
import hs.project.album.R
import hs.project.album.databinding.FragmentSettingBinding
import hs.project.album.databinding.FragmentStoryBinding
import hs.project.album.view.MainActivity

class StoryFrag : BaseFragment<FragmentStoryBinding>(R.layout.fragment_story) {

    companion object {
        const val TAG : String = "근황 프래그먼트"
        fun newInstance(): StoryFrag {
            return StoryFrag()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

    }

}