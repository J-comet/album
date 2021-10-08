package hs.project.album.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hs.project.album.data.AddPhotoData

class UserAlbumVM : ViewModel() {

    var vmAlbumList = MutableLiveData<ArrayList<String>?>()
    var albumItems: ArrayList<String> = ArrayList()

    var vmImgList = MutableLiveData<ArrayList<AddPhotoData>?>()
    var imgItems: ArrayList<AddPhotoData> = ArrayList()

    fun addAlbum(item: String) {
        albumItems.add(item)
        vmAlbumList.value = albumItems
    }

    fun removeAlbum(item: String) {
        albumItems.remove(item)
        vmAlbumList.value = albumItems
    }

    fun addImg(item: AddPhotoData) {
        imgItems.add(item)
        vmImgList.value = imgItems
    }

    fun removeImg(item: AddPhotoData) {
        imgItems.remove(item)
        vmImgList.value = imgItems
    }

    fun clear() {
        vmAlbumList.value = null
        vmImgList.value = null
    }
}