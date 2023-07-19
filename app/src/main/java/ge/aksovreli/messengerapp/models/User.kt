package ge.aksovreli.messengerapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var nickname: String? = "",
    var password: String? = "",
    var email: String? = (nickname?.replace("_", "__")?.replace(" ", "_") ?: "") + "@messenger.com",
    var profession: String? = "",
    var imgURI: String? = "",
)