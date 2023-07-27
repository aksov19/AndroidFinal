package ge.aksovreli.messengerapp

data class MessageItem (
    var senderUid: String? = "",
    var message: String? = "",
    var time: Long? = 0,
    var audioUri: String? = ""
)
