package hs.project.album

import android.app.Application
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import hs.project.album.util.PreferenceUtil


class MyApplication : Application(){

    companion object {
        lateinit var INSTANCE: MyApplication
            private set
        lateinit var firebaseAuth : FirebaseAuth
        lateinit var fireStoreDB: FirebaseFirestore
        lateinit var storage: FirebaseStorage

        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
        Firebase.initialize(this)
        INSTANCE = this
        firebaseAuth = FirebaseAuth.getInstance()
        fireStoreDB = Firebase.firestore
        storage = Firebase.storage
    }

    fun getAppName(): String = applicationInfo.loadLabel(packageManager).toString()

}