package friendroid.bustracking.activities

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import friendroid.bustracking.*
import friendroid.bustracking.R
import friendroid.bustracking.receivers.NotificationActionReceiver
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*
import kotlin.math.abs

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker
    // Location monitor
    private lateinit var monitor: EventListener<DocumentSnapshot>
    private lateinit var reference: DocumentReference
    private val transportOffice = LatLng(22.476514, 91.790843)
    private lateinit var query: Query
    private lateinit var listener: EventListener<QuerySnapshot>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Create monitor
        monitor = EventListener<DocumentSnapshot> { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
            } else {
                if (snapshot != null && snapshot.exists()) {
                    System.out.println("Here are :: ${snapshot["lat_lon"]}")
                    if (snapshot["online"] != true) {
                        updateLocation(null, null, snapshot["name"]?.toString()
                                ?: "Unknown")
                        return@EventListener
                    }
                    val geoPoint = snapshot["lat_lon"] as GeoPoint?
                    var latLng: LatLng? = null
                    if (geoPoint != null)
                        latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                    updateLocation(snapshot["location"], latLng, snapshot["name"]?.toString()
                            ?: "Unknown")
                } else {
                    // offline
                    updateLocation(null, null)
                }
            }
        }

        reference = FirebaseFirestore.getInstance().also { it.firestoreSettings = fireSettings }.document(
                "users/${intent.getStringExtra(EXTRA_BUS_ID) ?: "NA"}")
        if (mUser.role == "admin")
            query = FirebaseFirestore.getInstance().also { it.firestoreSettings = fireSettings }.collection("users")
                    .whereEqualTo("role", "driver").whereEqualTo("approved", true)
                    .whereEqualTo("online", true)
        else query = FirebaseFirestore.getInstance().also { it.firestoreSettings = fireSettings }.collection("users")
                .whereEqualTo("approved", true).whereEqualTo("role", "driver")
                .whereEqualTo("online", true).whereArrayContains("subscribers", mUser.uid)
        sharedPreferences = getSharedPreferences("msg_time", Context.MODE_PRIVATE)
        listener = EventListener<QuerySnapshot> { snap, err ->
            if (err != null) {
                err.printStackTrace()
            } else if (snap != null)
                for (document in snap.documents) {
                    checkMessage(document)
                }
        }
    }

    private fun checkMessage(bus: DocumentSnapshot?) {
        bus ?: return
        // Check message
        val time = bus["msg_time"] as Date?
        val msgTime = time?.toString() ?: "NA"
        val msg = bus["message"] as String? ?: ""

        val lastReceived = sharedPreferences.getString(bus["uid"] as String? ?: "NA", "")
        if (lastReceived != msgTime) {
            // Keep this time saved
            val editor = sharedPreferences.edit()
            editor.putString(bus["uid"] as String? ?: "NA", msgTime)
            editor.apply()

            if (msg.isBlank()) return
            // Show message notification
            val i = Intent(this, NotificationActionReceiver::class.java)
            val nid = abs(((bus["uid"]?.hashCode()?.dec() ?: 1) % 100) + 1)
//            System.out.println("Here are nid $nid")
            i.putExtra(EXTRA_NOTIFICATION_ID, nid)
            val builder = NotificationCompat.Builder(this, CHANEL_ID)
//                    .setContentIntent(PendingIntent.getBroadcast(activity, nid, i, 0))
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setContentTitle(bus["name"]?.toString())
                    .setContentText(msg)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                    .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .addAction(android.R.drawable.ic_notification_clear_all, getString(R.string.dismiss),
                            PendingIntent.getBroadcast(this, nid, i, 0))
            if (time != null) {
                builder.setShowWhen(true)
                builder.setWhen(time.time)
            }
            val manager: NotificationManagerCompat = NotificationManagerCompat.from(this)

            manager.notify(nid, builder.build())
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Cu, Chittagong.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        marker = mMap.addMarker(MarkerOptions().position(transportOffice)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
        )

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16f))
    }

    private fun updateLocation(loc: Any?, latLng: LatLng?, name: String = "Unknown") {

        if (loc == null || loc == "offline") {
            // set offline
            locationText.text = "$name : ${getString(R.string.offline)}"
        } else {
            // set name and location
            locationText.text = "$name : $loc"
            // animate marker
            latLng ?: return
            MarkerAnimation.animateMarkerToGB(marker, latLng, LatLngInterpolator.LinearFixed())
            marker.title = name
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(geoPoint.latitude, geoPoint.longitude)))
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(geoPoint.latitude, geoPoint.longitude), 15f))
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(latLng)
                    .zoom(mMap.cameraPosition.zoom).build()),
                    5000, null)

        }
    }

    override fun onStart() {
        super.onStart()
        reference.addSnapshotListener(monitor)
        query.addSnapshotListener(listener)
    }

    override fun onRestart() {
        super.onRestart()
        reference.addSnapshotListener(monitor)
    }
}
