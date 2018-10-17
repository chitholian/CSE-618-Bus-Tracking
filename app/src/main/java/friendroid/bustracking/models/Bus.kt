package friendroid.bustracking.models

open class Bus : User() {
    override var role = "driver"
    var online = false
    var subscribers = ArrayList<Any?>()
}
