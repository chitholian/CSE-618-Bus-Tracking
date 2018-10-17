package friendroid.bustracking.activities

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.firebase.firestore.*
import friendroid.bustracking.*
import friendroid.bustracking.R
import friendroid.bustracking.models.OnlineBus
import friendroid.bustracking.models.User
import friendroid.bustracking.services.LocationUpdaterService
import kotlinx.android.synthetic.main.activity_bus_driver.*
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.Exception

// Permission request code
const val REQ_CODE_1 = 111
const val REQ_CODE_2 = 222

class BusDriverActivity : BaseActivity(), ServiceStateListener {
    companion object {
        var stoppedByUser = false
    }

    // listen to user approval or deletion
    private lateinit var snapshotListener: EventListener<DocumentSnapshot>
    private lateinit var reference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        fragment_container.addView(View.inflate(this, R.layout.activity_bus_driver, null))
        reference = FirebaseFirestore.getInstance().document("users/${mUser.uid}")

        // create snapshot lister
        snapshotListener = EventListener<DocumentSnapshot> { snapshot, err ->
            if (err != null) {
                err.printStackTrace()
            } else if (snapshot != null && snapshot.exists()) {
                // Check if user approved or not approved
                val user = snapshot.toObject(OnlineBus::class.java)
//                mUser = user ?: User()
                title = user?.name
                // if approved simply hide the waiting message and show the components
                if (user?.approved == true) {
                    waiting.visibility = View.GONE
                    fragment_container.visibility = View.VISIBLE
                } else {
                    // hide components, stop services, show waiting msg
                    stopMyServices()
                    fragment_container.visibility = View.GONE
                    waiting.visibility = View.VISIBLE
                }
            } else {
                // User is deleted or something like that, so logout.
                stopMyServices()
                AuthUI.getInstance().signOut(this)
                mUser = User()
                toast(R.string.user_deleted)
                finish()
            }

            // hide progressbar if it is showing
            if (progressBar?.visibility == View.VISIBLE) progressBar?.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bus_driver, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                confirm(this, R.string.confirm_logout) {
                    stopMyServices()
                    AuthUI.getInstance().signOut(this)
                    mUser = User()
                    finish()
                }
            }
            R.id.menu_about -> showAbout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        confirmBeforeExit()
    }

    override fun onStart() {
        super.onStart()
        // show progressbar for loading user status
        progressBar.visibility = View.VISIBLE
        // Monitor if this user is approved
        reference.addSnapshotListener(this, snapshotListener)

        // to send message
        button_broadcast_msg.setOnClickListener { it ->
            val msg = message_field.text.trim().toString()
            if (msg.isEmpty()) {
                message_field.error = getString(R.string.enter_msg)
                message_field.requestFocus()
            } else {
                it.isEnabled = false
                progressBar.visibility = View.VISIBLE

                // send the message
                reference.update(mapOf("message" to msg, "msg_time" to FieldValue.serverTimestamp()))
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                                toast(getString(R.string.broadcast_sent))
                            else toast(getString(R.string.broadcast_failed))
                            // enable send button
                            it?.isEnabled = true
                            progressBar?.visibility = View.INVISIBLE
                        }
            }
        }

        // start/stop location update
        broadcast.setOnClickListener {
            if ((it as SwitchCompat).isChecked) {
                // Check permission
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    it.isChecked = false
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_CODE_1)
                } else
                // start broadcasting location
                    startMyServices()
            } else {
                // stop broadcast service
                stopMyServices()
            }
        }
    }

    private fun startMyServices() {


        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 7000
            fastestInterval = 5000
            smallestDisplacement = 10f
        }
        val settingClient = LocationSettingsRequest.Builder().addAllLocationRequests(listOf(locationRequest))
        // Check settings
        LocationServices.getSettingsClient(this).checkLocationSettings(settingClient.build())
                .addOnFailureListener {

                    if (it is ResolvableApiException) {
                        // Ask change settings
                        try {
                            it.startResolutionForResult(this@BusDriverActivity, REQ_CODE_2)
                        } catch (ex: Exception) {
                            Log.e("ERRRR", "Error here...")
                            ex.printStackTrace()
                        }
                    } else {
                        toast(R.string.settings_incompatible)
                        stopMyServices()
                    }
                }.addOnSuccessListener {
                    // Start service
                    val intent = Intent(this, LocationUpdaterService::class.java)
                    intent.action = ACTION_START_FOREGROUND
                    startService(intent)

                }

    }

    private fun stopMyServices() {
        val intent = Intent(this, LocationUpdaterService::class.java)
        intent.action = ACTION_STOP_FOREGROUND
        startService(intent)
        broadcast.isChecked = false
    }

    override fun onResume() {
        super.onResume()
        broadcast.isChecked = LocationUpdaterService.isServiceRunning
        title = mUser.name
        LocationUpdaterService.startStopListener.add(this)
    }

    override fun onPause() {
        super.onPause()
        LocationUpdaterService.startStopListener.remove(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Now we can start service
            startMyServices()
        } else {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_2)
            if (resultCode == Activity.RESULT_OK) {
                startMyServices()
            } else {
                toast(R.string.turn_on_location)
            }
    }

    override fun onStartService(service: Service) {
        super.onStartService(service)
        broadcast?.isChecked = true

    }

    override fun onStopService(service: Service) {
        super.onStopService(service)
        broadcast?.isChecked = false
    }
}
