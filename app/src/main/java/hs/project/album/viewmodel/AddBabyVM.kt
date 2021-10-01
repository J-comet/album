package hs.project.album.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hs.project.album.data.AddBabyData

class AddBabyVM : ViewModel() {

    var vmAddBabyData = MutableLiveData<AddBabyData?>()
//    val vmBirthCount = MutableLiveData<String?>()

    fun setData(addBabyData: AddBabyData) {
        vmAddBabyData.value = addBabyData
    }

//    fun setBirthCount(strCount: String){
//        vmBirthCount.value = strCount
//    }

    fun clear(){
        vmAddBabyData.value = null
    }

}