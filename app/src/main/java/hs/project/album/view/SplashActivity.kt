package hs.project.album.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import hs.project.album.BaseActivity
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.databinding.ActivitySplashBinding
import hs.project.album.view.user.LoginActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginUserCheck()
    }

    private fun loginUserCheck() {
        val loginStatus = MyApplication.prefs.getString(Constant.PREFERENCE_KEY.LOGIN_USER_ID,"none")
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
}