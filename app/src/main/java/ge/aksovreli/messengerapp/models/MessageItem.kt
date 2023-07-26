package ge.aksovreli.messengerapp.models

data class MessageItem (
    var senderUid: String? = "",
    var message: String? = "",
    var time: Long? = 0
)
