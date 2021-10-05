package hs.project.album.view.add


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import hs.project.album.Constant
import hs.project.album.MyApplication
import hs.project.album.R
import hs.project.album.data.AddPhotoData
import hs.project.album.databinding.DialogAddImageBinding
import hs.project.album.util.displayToast
import hs.project.album.util.getCurrentDateTime
import hs.project.album.util.hide
import hs.project.album.util.visible
import hs.project.album.view.MainActivity
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class AddImageDialog(selectedImgUri: Uri) : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogAddImageBinding
    private var saveType = "none"
    private val selectedUri = selectedImgUri
    private var imgList = arrayListOf<AddPhotoData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
        isCancelable = true // 화면밖 or 뒤로가기버튼으로 dialog dismiss = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.btnAll.setOnClickListener(this)
        binding.btnLimit.setOnClickListener(this)
        binding.ivImage.setOnClickListener(this)
        binding.clayoutBtnRegister.setOnClickListener(this)

        //  최초 선택상태
        selectedStatus(
            selectedAll = true,
            selectedLimit = false
        )
        saveType = binding.tvAll.text.toString()

        Glide.with(this)
            .load(selectedUri)
            .transform(RoundedCorners(12))
            .into(binding.ivImage)

        /**
         * 비율 맞춰서 출력하는 법 찾기
         */
    }

    private fun selectedStatus(
        selectedAll: Boolean,
        selectedLimit: Boolean
    ) {
        binding.btnAll.isSelected = selectedAll
        binding.tvAll.isSelected = selectedAll

        binding.btnLimit.isSelected = selectedLimit
        binding.tvLimit.isSelected = selectedLimit
    }


    /**
     * FirebaseStorage
     * Local File Upload
     */
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {

        binding.loadingView.visible()

        val albumUid = MyApplication.prefs.getString(Constant.PREFERENCE_KEY.USE_ALBUM_ID, "none")

        val fileName = "${getCurrentDateTime()}_${UUID.randomUUID()}.png"

        MyApplication.storage.reference.child("${albumUid}/photo")
            .child(fileName)
            .putFile(uri)
            .addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    MyApplication.storage.reference
                        .child("${albumUid}/photo")
                        .child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    Log.e("AddImageDialog", uploadTask.exception.toString())
                    errorHandler()
                }

//                binding.loadingView.hide(1)
            }

    }

    /**
     * FirebaseStorage
     * Stream Upload , MetaData
     */
    private fun streamUpload() {
        try {
            val contentResolver = requireActivity().contentResolver
            val storageMetadata = StorageMetadata.Builder()
                .setContentType(contentResolver.getType(selectedUri))
                .build()
            FirebaseStorage.getInstance().reference
                .child("posts")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(UUID.randomUUID().toString())
                .putStream(contentResolver.openInputStream(selectedUri)!!, storageMetadata)
                .addOnSuccessListener(requireActivity()) { task ->
                    val downloadUrl: Uri? = task.uploadSessionUri
                    requireActivity().displayToast("성공 성공 $downloadUrl")
                }
                .addOnFailureListener(
                    requireActivity()
                ) {
                    requireActivity().displayToast("실패 실패")
                }
        } catch (e: IOException) {
            Log.e("AddImageDialog", e.toString())
        }

        binding.loadingView.hide(1)
    }

    /**
     * 1. 저장된 image_list 가져오기
     * 2. list 에 새로운 이미지 추가
     * 3. 다시 fireStore 에 저장
     * album_list 에서 image_list 를 가져 온 후 list.add 추가 필요
     */
    private fun getAlbumImgList(imgFile: AddPhotoData) {

        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.ALBUM_LIST)
            .document(MyApplication.prefs.getString(Constant.PREFERENCE_KEY.USE_ALBUM_ID, "none"))
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    imgList = document["image_list"] as ArrayList<AddPhotoData>
                    imgList.add(imgFile)
                    imgUpload(imgList)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddImageDialog", "get failed with ", exception)
            }

    }

    private fun imgUpload(imgList: ArrayList<AddPhotoData>){
        val data = hashMapOf("image_list" to imgList)

        MyApplication.fireStoreDB.collection(Constant.FIREBASE_DOC.ALBUM_LIST)
            .document(MyApplication.prefs.getString(Constant.PREFERENCE_KEY.USE_ALBUM_ID, "none"))
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                dismiss()
            }
            .addOnFailureListener { exception ->
                Log.e("AddImageDialog", "get failed with ", exception)
            }

        binding.loadingView.hide(1)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            binding.btnAll.id -> {
                selectedStatus(selectedAll = true, selectedLimit = false)
                saveType = binding.tvAll.text.toString()
            }
            binding.btnLimit.id -> {
                selectedStatus(selectedAll = false, selectedLimit = true)
                saveType = binding.tvLimit.text.toString()
            }
            binding.ivImage.id -> {
                dismiss()
                (activity as MainActivity).openGallery()
            }
            binding.clayoutBtnRegister.id -> {
                /**
                 *  FireStore 에 저장해야 될 데이터
                 *  'userUid', 'albumUid' , 'image' , 'saveType' , 'date'
                 */

//                streamUpload()
                val photoUri = selectedUri
                uploadPhoto(photoUri,
                    successHandler = { url -> // 다운로드 url 을 받아서 처리

                        /**
                         *  'album_uid' preference 에 저장한 값 불러오기
                         *  preference 값이 없을 때는 앨범생성 때 값 설정
                         *  값이 있다면 설정에서 유저가 변경할 때 값 설정
                         */

                        // RecyclerView 에 들어갈 데이터
                        AddPhotoData(
                            user_uid = MyApplication.firebaseAuth.currentUser?.uid,
//                            album_uid = MyApplication.prefs.getString(
//                                Constant.PREFERENCE_KEY.USE_ALBUM_ID,
//                                "none"
//                            ),
                            url = url,
                            save_type = saveType,
                            date = getCurrentDateTime()
                        )

                        // fireStore 'album_list' 문서의 'image_list' 에 'AddPhotoData' 저장
                        getAlbumImgList(
                            AddPhotoData(
                            user_uid = MyApplication.firebaseAuth.currentUser?.uid,
//                            album_uid = MyApplication.prefs.getString(
//                                Constant.PREFERENCE_KEY.USE_ALBUM_ID,
//                                "none"
//                            ),
                            url = url,
                            save_type = saveType,
                            date = getCurrentDateTime()
                        ))

                    },
                    errorHandler = {
                        if (activity != null && isAdded) {
                            requireActivity().displayToast("사진 업로드 실패")
                        }
                    })
            }

        }
    }
}