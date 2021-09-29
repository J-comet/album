package hs.project.album.view.album

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import hs.project.album.BaseFragment
import hs.project.album.R
import hs.project.album.databinding.FragmentAlbumBinding
import hs.project.album.databinding.FragmentAlbumView01Binding

class AlbumView01Frag : BaseFragment<FragmentAlbumView01Binding>(R.layout.fragment_album_view_01) {

    companion object{
        const val TAG = "AlbumViewPager01"
        fun newInstance(): AlbumView01Frag {
            return AlbumView01Frag()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}