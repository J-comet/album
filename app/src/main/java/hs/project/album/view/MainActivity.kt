package hs.project.album.view


import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.badge.BadgeDrawable
import hs.project.album.BaseActivity
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.databinding.ActivityMainBinding
import hs.project.album.view.add.AddImageDialog
import hs.project.album.view.album.AlbumFrag
import hs.project.album.view.family.FamilyFrag
import hs.project.album.view.setting.SettingFrag
import hs.project.album.view.story.StoryFrag
import hs.project.album.view.user.LoginActivity
import hs.project.album.view.user.SignupActivity


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main)  {

    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>
//    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    companion object {
        const val TAG: String = "MainActivity"
        const val REQUEST_CODE_PERMISSIONS = 1001
        const val REQUEST_GALLERY_PERMISSIONS = 1002

        private const val PERMISSION_READ_EXTERNAL_STORAGE =
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE =
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frlayout_main, AlbumFrag.newInstance())
            .commit()

        // permission check
        permissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it.all { permission -> permission.value == true }
                ) {
                    openGallery()
                } else {
                    showToast(resString(R.string.str_common_08))
                }
            }

        // camera open
//        cameraLauncher =
//            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
//                if (success) {
//                    // The image was saved into the given Uri -> do something with it
//
//                } else {
//                    Toast.makeText(this, "카메라 취소", Toast.LENGTH_SHORT).show()
//                }
//            }

        galleryLauncher =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val currentImageUri = result.data?.data
                    currentImageUri?.let {
                        AddImageDialog(it).show(
                            supportFragmentManager,
                            "AddImageDialog"
                        )
                    }
                    Log.d("이미지", "$currentImageUri")
                } else if (result.resultCode == RESULT_CANCELED) {
                    showToast(resString(R.string.str_cancel_selected))
                }
            }
    }



    private fun init() {

        val menuItemId: Int = binding.bottomNavView.menu.getItem(0).itemId // 0 menu item index.
        val badgeDrawable = binding.bottomNavView.getOrCreateBadge(menuItemId)
        badgeDrawable.isVisible = true

        badgeDrawable.badgeGravity = BadgeDrawable.TOP_END    // badge 위치
//        BadgeDrawable.TOP_START
//        BadgeDrawable.BOTTOM_END
//        BadgeDrawable.BOTTOM_START

//        badgeDrawable.number = 10
//        badgeDrawable.clearNumber() // badge 숫자 없애기

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, AlbumFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "앨범 클릭")
                    badgeDrawable.isVisible = false   // badge 없애기
                }
                R.id.page_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, StoryFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "근황 클릭")

                }
                R.id.page_3 -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, FamilyFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "가족 클릭")

                }
                R.id.page_4 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, SettingFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "설정 클릭")

                }
            }
            return@setOnItemSelectedListener true
        }

        binding.fbAddImage.setOnClickListener {
            if (!checkPermission(PERMISSIONS_REQUESTED)) {
                permissionResultLauncher.launch(PERMISSIONS_REQUESTED)
            } else {
                openGallery()
            }
        }
    }

//    private fun openPictureDialog() {
//        val list = arrayOf("카메라", "앨범")
//        val builder = AlertDialog.Builder(this)
//            .setTitle("사진 등록")
//            .setItems(list) { _, which ->
//                when (which) {
//                    0 -> {  // 카메라
//
//                        Toast.makeText(this, "CAMERA", Toast.LENGTH_SHORT).show()
//                    }
//                    1 -> {  // 앨범
//
//
//                    }
//                }
//            }
//        builder.show()
//    }

    fun openGallery() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    /**
     * 권한 체크
     */
    private fun checkPermission(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 권한 요청 결과
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    openGallery()
                } else {
                    showToast(resString(R.string.str_common_08))
                }
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

//    override fun onBackPressed() {
//
//        val count = supportFragmentManager.backStackEntryCount
//
//        if (count > 0) {
//            supportFragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }
//
//    }


}

