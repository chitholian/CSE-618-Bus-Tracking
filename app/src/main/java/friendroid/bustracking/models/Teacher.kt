package friendroid.bustracking.models

open class Teacher : User() {
    override var role = "teacher"
    var buses: List<Bus> = ArrayList<Bus>()
}
