package friendroid.bustracking.models

import com.google.firebase.firestore.GeoPoint
import java.util.*

open class OnlineBus : Bus() {
    var location = ""
    //var lat_lon = GeoPoint(0.0, 0.0)
    var message = ""
    //var msg_time = Date()
}
