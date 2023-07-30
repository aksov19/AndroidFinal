package ge.aksovreli.messengerapp.models

data class ChatItem (
    var name: String,
    var last_message: String = "",
    var date: String ="",
    var avatar: String = "",
    var uid: String? = ""
)