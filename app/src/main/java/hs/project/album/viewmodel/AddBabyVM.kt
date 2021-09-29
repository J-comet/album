package hs.project.album.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class AddBabyVM : ViewModel() {

    val name = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val birthday = MutableLiveData<String>()

    fun sendData(strName: String, strGender: String, strBirthDay: String) {
        name.value = strName
        gender.value = strGender
        birthday.value = strBirthDay
    }

}