package hs.project.album.view.user

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import hs.project.album.BaseActivity
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.adapter.SlideViewPagerAdapter
import hs.project.album.databinding.ActivityLoginBinding
import hs.project.album.dialog.CommonDialog
import hs.project.album.util.*
import hs.project.album.view.MainActivity


class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login),
    View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    private fun init() {
        binding.btnLogin.setOnClickListener(this)
        binding.btnSignup.setOnClickListener(this)
        binding.btnSimpleSignup.setOnClickListener(this)

        binding.slideViewPager.adapter = SlideViewPagerAdapter(getSlideList())
        binding.slideViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.slideViewPager.setPageTransformer(DepthPageTransformer())  // animation

        binding.slideViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        setupIndicators(getSlideList().size)
        setTextInputLayoutIconColor()
    }

    // 뷰페이저 아이템
    private fun getSlideList(): ArrayList<Int> {
        return arrayListOf(
            R.drawable.slide_img_04,
            R.drawable.slide_img_01,
            R.drawable.slide_img_02,
            R.drawable.slide_img_03
        )
    }

    private fun setupIndicators(count: Int) {
        val indicators: Array<ImageView?> = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 8, 8, 8)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.bg_indicator_inactive
                )
            )
            indicators[i]?.layoutParams = params
            binding.layoutIndicators.addView(indicators[i])
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount: Int = binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView: ImageView = binding.layoutIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                    )
                )
            }
        }
    }

    private fun setTextInputLayoutIconColor() {
        binding.tilEmail.setStartIconTintList(
            ContextCompat.getColorStateList(
                this,
                R.color.icon_selector
            )
        )
        binding.tilPassword.setStartIconTintList(
            ContextCompat.getColorStateList(
                this,
                R.color.icon_selector
            )
        )
        binding.tilPassword.setEndIconTintList(
            ContextCompat.getColorStateList(
                this,
                R.color.icon_selector
            )
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnLogin.id -> {

                when {
                    binding.etEmail.text.toString().isEmpty() -> {
                        displayToast(resString(R.string.str_email_input))
                    }
                    binding.etPassword.text.toString().isEmpty() -> {
                        displayToast(resString(R.string.str_password_input))
                    }
                    else -> {
                        if (isNetworkConnected()) {
                            binding.loadingView.visible()
                            // 강제로 loading 화면 천천히 닫히게 하기위해 handler
                            Handler(mainLooper).postDelayed({
                                login(
                                    binding.etEmail.text.toString(),
                                    binding.etPassword.text.toString()
                                )
                            }, 600)
                        } else {
                            val dialog = CommonDialog(resString(R.string.str_network_fail))
                            dialog.show(supportFragmentManager, "CommonDialog")
                            dialog.setOnClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun onClicked() {
                                    dialog.dismiss()
                                }
                            })
                        }
                    }
                }


            }
            binding.btnSignup.id -> {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }
            binding.btnSimpleSignup.id -> {

            }
        }
    }

    private fun login(email: String, password: String) {

        MyApplication.firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { loginTask ->
                if (loginTask.isSuccessful) {
                    if (MyApplication.firebaseAuth.currentUser?.isEmailVerified!!) {

                        /**
                         *  추후에 로그인 확인 작업은 preference 에 저장된 값으로 확인할 것
                         */
                        MyApplication.prefs.setString(Constant.PREFERENCE_KEY.LOGIN_USER_ID, MyApplication.firebaseAuth.currentUser!!.uid)

                        val i = Intent(this, MainActivity::class.java)
                        i.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                    } else {
                        showSnackBar(binding.btnLogin.rootView,R.string.str_verify_email,2000)
                    }
                } else {
                    displayToast(resString(R.string.str_check_info))
                }
            }

        // 강제로 loading 화면 띄우기 위해 handler
        Handler(mainLooper).postDelayed({
            binding.loadingView.hide(0)
        }, 600)
    }


    // Animation
    inner class DepthPageTransformer : ViewPager2.PageTransformer {

        private val MIN_SCALE = 0.75f

        override fun transformPage(view: View, position: Float) {

            view.translationX = -1 * view.width * position

            view.apply {
                val pageWidth = width
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 0 -> { // [-1,0]
                        // Use the default slide transition when moving to the left page
                        alpha = 1f
                        translationX = 0f
                        translationZ = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                    position <= 1 -> { // (0,1]
                        // Fade the page out.
                        alpha = 1 - position

                        // Counteract the default slide transition
                        translationX = pageWidth * -position
                        // Move it behind the left page
                        translationZ = -1f

                        // Scale the page down (between MIN_SCALE and 1)
                        val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }

}