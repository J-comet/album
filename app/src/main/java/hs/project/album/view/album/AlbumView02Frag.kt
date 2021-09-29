package hs.project.album.view.album

import android.os.Bundle
import android.view.View
import hs.project.album.BaseFragment
import hs.project.album.R
import hs.project.album.databinding.FragmentAlbumView02Binding
import hs.project.album.dialog.CreateAlbumDialog

class AlbumView02Frag : BaseFragment<FragmentAlbumView02Binding>(R.layout.fragment_album_view_02),View.OnClickListener {

    companion object{
        const val TAG = "AlbumViewPager02"
        fun newInstance(): AlbumView02Frag {
            return AlbumView02Frag()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        binding.needAlbum.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.needAlbum.id -> {
                CreateAlbumDialog().show(
                    childFragmentManager,
                    "CreateAlbumDialog"
                )
            }
        }
    }

}