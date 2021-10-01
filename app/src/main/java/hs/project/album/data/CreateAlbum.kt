package hs.project.album.data

data class CreateAlbum(
    var user_uid : ArrayList<String>? = null, // 최초 생성자
    var album_name : String? = null,  // 앨범이름
    var master_uid_list : String? = null, // 엄마,아빠만 마스터 기능 사용 가능
    var image_list: String? = null, // 사진들
    var user_list: String? = null, // 앨범 공유하고 있는 유저들
    var baby_list: String? = null, // 아기 리스트
    var date: String? = null // 앨범 생성일
)
