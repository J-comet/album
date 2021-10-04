package hs.project.album.view.user

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import hs.project.album.BaseActivity
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.databinding.ActivitySignupBinding
import hs.project.album.dialog.CommonDialog
import hs.project.album.util.*


class SignupActivity : BaseActivity<ActivitySignupBinding>(R.layout.activity_signup),
    View.OnClickListener {
    private var validEmailCheck = false
    private var validPasswordCheck = false
    private var validPasswordConfirm = false
    private var validSignUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding.btnSignup.setOnClickListener(this)

        binding.tilEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                regexEmailCheck()
            }
        })

        binding.tilPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                regexPasswordCheck()
            }
        })

        binding.tilPasswordCheck.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                passwordConfirm()
            }
        })
    }

    private fun isPasswordFormat(password: String): Boolean {
        return password.matches("^(?=.*[a-zA-z])(?=.*[0-9])(?!.*[^a-zA-z0-9]).{9,16}\$".toRegex()) // 정규식 패턴 (영문자/숫자 포함 9 ~ 16글자)
    }

    private fun isEmailFormat(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun signUpBtnStatus(emailStatus: Boolean, pwStatus: Boolean, pwConfirmStatus: Boolean) {
        if (emailStatus && pwStatus && pwConfirmStatus) {
            binding.btnSignup.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_4facfe))
            validSignUp = true
        } else {
            binding.btnSignup.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_d3d3d3))
            validSignUp = false
        }
    }

    private fun regexEmailCheck() {  // 이메일 정규식체크
        val email: String = binding.tilEmail.editText?.text.toString()

        if (email.isEmpty()) {
            binding.tilEmail.error = resString(R.string.str_email_input)
            validEmailCheck = false
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        } else if (!isEmailFormat(email)) {
            binding.tilEmail.error = resString(R.string.str_email_format)
            validEmailCheck = false
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        } else {
            binding.tilEmail.error = null
            binding.tilEmail.isErrorEnabled = false
            validEmailCheck = true
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        }
    }

    private fun regexPasswordCheck() {  // 패스워드 정규식체크
        val password = binding.tilPassword.editText?.text.toString()

        if (password.isEmpty()) {
            binding.tilPassword.error = resString(R.string.str_password_input)
            validPasswordCheck = false
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        } else if (!isPasswordFormat(password)) {
            binding.tilPassword.error = resString(R.string.str_password_format)
            validPasswordCheck = false
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        } else {
            binding.tilPassword.error = null
            binding.tilPassword.isErrorEnabled = false
            validPasswordCheck = true
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        }
    }

    private fun passwordConfirm() {  // 비밀번호 확인
        val password = binding.tilPassword.editText?.text.toString()
        val passwordCheck = binding.tilPasswordCheck.editText?.text.toString()

        if (passwordCheck.isEmpty()) {
            binding.tilPasswordCheck.error = resString(R.string.str_password_check_input)
            validPasswordConfirm = false
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        } else if (!password.equals(passwordCheck)) {
            binding.tilPasswordCheck.error = resString(R.string.str_password_check)
            validPasswordConfirm = false
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        } else {
            binding.tilPasswordCheck.error = null
            binding.tilPasswordCheck.isErrorEnabled = false
            validPasswordConfirm = true
            signUpBtnStatus(validEmailCheck, validPasswordCheck, validPasswordConfirm)
        }
    }

    private fun userSignUp(email: String, password: String) {

        /**
         * 파이어베이스 이메일 인증은 기본적으로 회원가입 이후에 할 수 있음
         * 회원가입 버튼 누를 때 인증메일 보낸 후 로그인 할 때 인증 했는지 체크
         */
        MyApplication.firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createTask ->
                if (createTask.isSuccessful) {
                    MyApplication.firebaseAuth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener(this) { verifyTask ->

                            if (verifyTask.isSuccessful) {
                                val uid = MyApplication.firebaseAuth.currentUser!!.uid
                                registerUserFireStore(uid,email)
                            } else {
                                displayToast(resString(R.string.str_send_email_fail))
                                Log.e("SignupActivity", verifyTask.exception.toString())
                            }
                            binding.loadingView.hide(0)
                        }
                } else {
                    displayToast(resString(R.string.str_signup_fail))
                    binding.loadingView.hide(0)
                    Log.e("SignupActivity", createTask.exception.toString())
                }
            }
    }

    // fireStore 에 userInfo 등록
    private fun registerUserFireStore(uid: String, email: String) {

        val albumList: MutableList<String> = ArrayList()

        val user = hashMapOf(
            "uid" to uid,
            "email" to email,
            "album_list" to albumList
        )

        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.USER_LIST).document(email)
            .set(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    displayToast(resString(R.string.str_send_email))
                    finish()
                }
            }
            .addOnSuccessListener { Log.d("SignupActivity", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.e("SignupActivity", "Error writing document", e) }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnSignup.id -> {
                if (isNetworkConnected()) {
                    if (validSignUp) {
                        binding.loadingView.visible()
                        userSignUp(
                            binding.tilEmail.editText?.text.toString(),
                            binding.tilPasswordCheck.editText?.text.toString()
                        )
                    } else {
                        displayToast(resString(R.string.str_common_07))
                    }
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
}