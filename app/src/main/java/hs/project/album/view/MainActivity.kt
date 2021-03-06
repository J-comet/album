package hs.project.album.view


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
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


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>

    //    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private lateinit var cropImgLauncher: ActivityResultLauncher<String>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var userAlbumVM: UserAlbumVM
    private var albumList: MutableList<String> = ArrayList()
    private var addPicture = false


    companion object {
        const val TAG: String = "MainActivity"
        const val REQUEST_CODE_PERMISSIONS = 1001
        const val REQUEST_GALLERY_PERMISSIONS = 1002
        const val REQUEST_CODE_CROP_IMAGE = 1003

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
        userAlbumVM = ViewModelProvider(this).get(UserAlbumVM::class.java)
        userAlbumVM.vmAlbumList.observe(this, { list ->

            addPicture = false

            if (list != null) {
                if (list.size > 0) {
                    addPicture = true
                }
            }

        })
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
//                    Toast.makeText(this, "????????? ??????", Toast.LENGTH_SHORT).show()
//                }
//            }

        // ??????????????? ?????? ??? (?????? deprecated ??? ?????? ?????? ???)
//        cropImgLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            uri.let {
//                AddImageDialog(it).show(
//                    supportFragmentManager,
//                    "AddImageDialog"
//                )
//            }
//        }

        galleryLauncher =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val currentImageUri = result.data?.data

                    currentImageUri?.let {
                        val cropRequest = CropRequest.Auto(
                            sourceUri = it,
                            requestCode = REQUEST_CODE_CROP_IMAGE
                        )

                        Croppy.start(this, cropRequest)

                    }
                    Log.d("?????????", "$currentImageUri")
                } else if (result.resultCode == RESULT_CANCELED) {
                    displayToast(resString(R.string.str_cancel_selected))
                }
            }



    }


    private fun init() {

        val menuItemId: Int = binding.bottomNavView.menu.getItem(0).itemId // 0 menu item index.
        val badgeDrawable = binding.bottomNavView.getOrCreateBadge(menuItemId)
        badgeDrawable.isVisible = true

        badgeDrawable.badgeGravity = BadgeDrawable.TOP_END    // badge ??????
//        BadgeDrawable.TOP_START
//        BadgeDrawable.BOTTOM_END
//        BadgeDrawable.BOTTOM_START

//        badgeDrawable.number = 10
//        badgeDrawable.clearNumber() // badge ?????? ?????????

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, AlbumFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "?????? ??????")
                    badgeDrawable.isVisible = false   // badge ?????????
                }
                R.id.page_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, StoryFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "?????? ??????")

                }
                R.id.page_3 -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, FamilyFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "?????? ??????")

                }
                R.id.page_4 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frlayout_main, SettingFrag.newInstance())
                        .addToBackStack(null)
                        .commit()
                    Log.d(TAG, "?????? ??????")

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

    private fun getUserAlbumList() {
        // user ???????????? album Idx List ?????????
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
//        val list = arrayOf("?????????", "??????")
//        val builder = AlertDialog.Builder(this)
//            .setTitle("?????? ??????")
//            .setItems(list) { _, which ->
//                when (which) {
//                    0 -> {  // ?????????
//
//                        Toast.makeText(this, "CAMERA", Toast.LENGTH_SHORT).show()
//                    }
//                    1 -> {  // ??????
//
//
//                    }
//                }
//            }
//        builder.show()
//    }

    fun openGallery() {
        if (addPicture) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        } else {

            val builder = AlertDialog.Builder(this)
                .setMessage(resString(R.string.str_common_13))
                .setCancelable(true)
                .setPositiveButton("??????") { dialogInterface: DialogInterface, i: Int ->
                    CreateAlbumDialog().show(
                        supportFragmentManager,
                        "CreateAlbumDialog"
                    )
                }
                .setNegativeButton("??????") { dialogInterface: DialogInterface, i: Int ->
                    displayToast(resString(R.string.str_common_13))
                }.show()
            builder.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.color_808080))
            builder.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    /**
     * ?????? ??????
     */
    private fun checkPermission(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * ?????? ?????? ??????
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

    /**
     * Croopy ????????? ????????????
     * ??? ?????? ??? deprecated ?????? ????????????
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            if (data != null) {
                data.data?.let {
                    AddImageDialog(it).show(
                        supportFragmentManager,
                        "AddImageDialog"
                    )
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

