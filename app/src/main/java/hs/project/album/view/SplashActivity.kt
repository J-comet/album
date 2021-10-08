package hs.project.album.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.SetOptions
import hs.project.album.BaseActivity
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.databinding.ActivitySplashBinding
import hs.project.album.dialog.AddBabyDialog
import hs.project.album.dialog.CommonDialog
import hs.project.album.util.displayToast
import hs.project.album.util.isNetworkConnected
import hs.project.album.util.resString
import hs.project.album.view.user.LoginActivity
import hs.project.album.viewmodel.UserAlbumVM

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private lateinit var userAlbumVM: UserAlbumVM
    private var albumList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userAlbumVM = ViewModelProvider(this).get(UserAlbumVM::class.java)

        if (isNetworkConnected()) {
//            getUserData()
            loginUserCheck()
        } else {
            val dialog = CommonDialog(resString(R.string.str_network_fail))
            dialog.show(supportFragmentManager, "CommonDialog")
            dialog.setOnClickListener(object : CommonDialog.OnDialogClickListener {
                override fun onClicked() {
                    finish()
                }
            })
        }
    }

    private fun loginUserCheck() {
        val loginStatus =
            MyApplication.prefs.getString(Constant.PREFERENCE_KEY.LOGIN_USER_ID, "none")
        if (loginStatus.equals("none")) {

            lottieAnimation()
            Handler(mainLooper).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 2200)

        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun lottieAnimation() {
        binding.loadingImage.playAnimation()
    }

    /**
     * 앱 실행시 유저 가입 앨범정보, 앨범의 이미지 정보 가져오기
     */
//    private fun getUserData() {
//
//        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.USER_LIST)
//            .document(MyApplication.firebaseAuth.currentUser?.email.toString())
//            .get()
//            .addOnSuccessListener { document ->
//
//                if (document.exists()){
//                    if (document["album_list"] != null) {
//                        albumList = document["album_list"] as ArrayList<String>
//
//                        if (albumList.size > 0) {
//                            for (item in albumList.indices){
//                                userAlbumVM.addAlbum(albumList[item])
//                            }
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                        Log.e("hs", "get failed with ", exception)
//                    }
//            }

}