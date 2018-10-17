package friendroid.bustracking.services

import android.app.IntentService
import android.content.Intent
import android.location.Geocoder
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import friendroid.bustracking.fireSettings
import friendroid.bustracking.mUser
import java.lang.Exception

class LocationNameResolverService : IntentService("LocationResolver") {
    override fun onHandleIntent(intent: Intent?) {
        intent ?: return
        val lat = intent.getDoubleExtra("lat", 22.476514)
        val lon = intent.getDoubleExtra("lon", 91.790843)
        var locationName = "Unknown Location Name"
        if (Geocoder.isPresent()) {
            val geocoder = Geocoder(this)
            try {
                val result = geocoder.getFromLocation(lat, lon, 1)
                if (result != null && result.isNotEmpty()) {
                    locationName = result[0].featureName
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        Log.e("BusTrackingLocation", "Lat: $lat, Lon: $lon, Loc: $locationName")
        // Upload location to server
        FirebaseFirestore.getInstance().also { it.firestoreSettings = fireSettings }
                .document("users/${mUser.uid}").update(mapOf("online" to true, "location" to locationName, "lat_lon" to GeoPoint(lat, lon)))
    }
}
