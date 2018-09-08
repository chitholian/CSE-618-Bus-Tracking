package friendroid.bustracking.entities

import com.google.firebase.database.Exclude

open class User {
    @Exclude
    var uid = ""

    var name = "Unknown"
    open var role = "Unknown"
    var identity = "Unknown"
    var photo = "Unknown"
    var approved = false
}
