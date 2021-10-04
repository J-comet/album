package hs.project.album.data

/**
 * 서버에 저장해야될 데이터
 *  'userUid', 'albumUid' , 'image' , 'saveType' , 'date'
 */
data class AddPhotoData(
    var user_uid : String? = null, // 최초 등록자
    var album_uid : String? = null, // 앨범 Uid
    var url : String? = null,  // 이미지 url
    var save_type: String? = null, // 사진 볼 수있는 범위
    var date: String? = null // 이미지 등록일
)
