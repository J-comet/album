package hs.project.album

object Constant {
    const val _WEATHER_BASIC_URL = "https://api.openweathermap.org/data/2.5/" // 날씨 API
    const val _WEATHER_API_KEY = "5b568d40b72524a011f52a3faa379669" // 날씨 API KEY
    const val _WEATHER_GET_IMAGE_URL = "http://openweathermap.org/img/wn/" // 날씨 ICON

    interface PREFERENCE_KEY {
        companion object {
            const val LOGIN_USER_ID = "current_uid"
            const val USE_ALBUM_ID = "current_album_uid"  // 현재 사용중인 앨범 uid
        }
    }

    interface FIREBASE_DOC {
        companion object{
            const val USER_LIST = "user_list"
            const val ALBUM_LIST = "album_list"
        }
    }
}