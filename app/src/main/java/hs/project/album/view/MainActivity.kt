package hs.project.album.view


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import com.google.android.material.badge.BadgeDrawable
import hs.project.album.BaseActivity
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.databinding.ActivityMainBinding
import hs.project.album.dialog.CreateAlbumDialog
import hs.project.album.util.displayToast
import hs.project.album.util.resString
import hs.project.album.view.add.AddImageDialog
import hs.project.album.view.album.AlbumFrag
import hs.project.album.view.family.FamilyFrag
import hs.project.album.view.setting.SettingFrag
import hs.project.album.view.story.StoryFrag
import hs.project.album.viewmodel.UserAlbumVM


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main)  {

    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>
//    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var userAlbumVM: UserAlbumVM
    private var albumList: MutableList<String> = ArrayList()
    private var addPicture = false

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
        getUserAlbumList()

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
                    displayToast(resString(R.string.str_common_08))
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
                    displayToast(resString(R.string.str_cancel_selected))
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

                if (addPicture){
                    openGallery()
                } else {

                    val builder = AlertDialog.Builder(this)
                        .setMessage(resString(R.string.str_common_13))
                        .setCancelable(true)
                        .setPositiveButton("이동") { dialogInterface: DialogInterface, i: Int ->
                            CreateAlbumDialog().show(
                                supportFragmentManager,
                                "CreateAlbumDialog"
                            )
                        }
                        .setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
                            displayToast(resString(R.string.str_common_13))
                        }.show()
                    builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.color_808080))
                    builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black))

                }
            }
        }
    }

    private fun getUserAlbumList() {
        // user 데이터의 album Idx List 가져옴
        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.USER_LIST)
            .document(MyApplication.firebaseAuth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    albumList = document["album_list"] as ArrayList<String>

                    addPicture = false

                    if (albumList.size > 0) {
                        addPicture = true
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("hs", "get failed with ", exception)
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
                    displayToast(resString(R.string.str_common_08))
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

