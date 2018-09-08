package friendroid.bustracking.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import friendroid.bustracking.R
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        // Add a marker in Cu and move the camera
        val lat = intent.getDoubleExtra("lat", 22.471038)
        val lon = intent.getDoubleExtra("lon", 91.7884657)
        val name = intent.getStringExtra("name")
        val loc = intent.getStringExtra("place")
        val cu = LatLng(lat, lon)
        mMap.addMarker(MarkerOptions().position(cu).title(name))
//        locationText.text = "<html>$name : <b>$loc</b></html>"
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        locationText.text = "$name : $loc"
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cu, 12.0f))
        mMap.addCircle(CircleOptions().center(cu).radius(20.0).strokeColor(Color.BLUE))

    }
}
