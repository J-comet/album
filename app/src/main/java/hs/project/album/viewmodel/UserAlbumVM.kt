package hs.project.album.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserAlbumVM : ViewModel() {

//    val vmAlbumList = MutableLiveData<MutableList<String>?>()

//    fun setData(albumList: MutableList<String>) {
//        vmAlbumList.value = albumList
//    }

    var vmAlbumList = MutableLiveData<MutableList<String>?>()
    var items: MutableList<String> = ArrayList()

    fun add(item: String) {
        items.add(item)
        vmAlbumList.value = items
    }

    fun remove(item: String) {
        items.remove(item)
        vmAlbumList.value = items
    }

    fun clear() {
        vmAlbumList.value = null
    }
}