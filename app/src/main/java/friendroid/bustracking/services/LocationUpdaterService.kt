package friendroid.bustracking.services

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import friendroid.bustracking.*
import friendroid.bustracking.R
import friendroid.bustracking.activities.BusDriverActivity
import friendroid.bustracking.models.User
import friendroid.bustracking.receivers.DeviceStateListener
import kotlin.collections.HashSet

class LocationUpdaterService : Service() {
    companion object {
        var isServiceRunning = false
        val startStopListener = HashSet<ServiceStateListener>()
    }

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallBack: LocationCallback

    private lateinit var reference: DocumentReference
    private lateinit var presenceRef: DatabaseReference
    private lateinit var receiver: BroadcastReceiver

    private var snapshotListener = EventListener<DocumentSnapshot> { snapshot, err ->
        if (err != null) {
            err.printStackTrace()
        } else if (snapshot != null && snapshot.exists()) {
            // Check if user approved or not approved
            val user = snapshot.toObject(User::class.java)
            // if approved simply hide the waiting message and show the components
            if (user?.approved != true) {
                stopMySelf()
            }
        } else {
            // User is deleted or something like that, so logout.
            stopMySelf()
            AuthUI.getInstance().signOut(this)
            Toast.makeText(this, R.string.user_deleted, Toast.LENGTH_LONG).show()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_STOP_FOREGROUND -> stopMySelf()
            ACTION_START_FOREGROUND -> startMySelf()
        }
        return START_STICKY
    }

    override fun onCreate() {
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                p0 ?: return
                for (location in p0.locations) {
//                    Toast.makeText(this@LocationUpdaterService, location.toString(), Toast.LENGTH_LONG).show()
                    // Upload the location using intent service
                    startService(Intent(this@LocationUpdaterService, LocationNameResolverService::class.java).also {
                        it.putExtra("lat", location.latitude)
                        it.putExtra("lon", location.longitude)
                    })
                }
            }
        }
        reference = FirebaseFirestore.getInstance().document("users/${mUser.uid}")
        presenceRef = FirebaseDatabase.getInstance().getReference("/status/${mUser.uid}")

        // register broadcast receiver
        receiver = DeviceStateListener()
        registerReceiver(receiver, IntentFilter().apply {
            addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION)
            addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        })
    }

    override fun onDestroy() {
        if (isServiceRunning) stopMySelf()
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun stopMySelf() {
        locationClient.removeLocationUpdates(locationCallBack)
        presenceRef.setValue("offline")
        stopForeground(true)
        stopSelf()
        if (isServiceRunning) {
            isServiceRunning = false
            for (listener in startStopListener) listener.onStopService(this)
            Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startMySelf() {
        reference.addSnapshotListener(snapshotListener)
        presenceRef.setValue("online")
        presenceRef.onDisconnect().setValue("offline")
        val targetIntent = PendingIntent.getActivity(this, 0, Intent(this, BusDriverActivity::class.java).also {
            it.action = ACTION_MAIN
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }, 0)

        // Build a notification
        val notification = NotificationCompat.Builder(this, CHANEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), 128, 128, false))
                .setContentTitle(getString(R.string.broadcasting))
                .setContentText(getString(R.string.sharing_location))
                .setContentIntent(targetIntent)
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 7000
            fastestInterval = 5000
            smallestDisplacement = 10f
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val settingClient = LocationSettingsRequest.Builder().addAllLocationRequests(listOf(locationRequest))
            // Check settings
            LocationServices.getSettingsClient(this).checkLocationSettings(settingClient.build())
                    .addOnSuccessListener {
                        locationClient.requestLocationUpdates(locationRequest, locationCallBack, null)
                    }.addOnFailureListener {
                        Toast.makeText(this, R.string.turn_on_location, Toast.LENGTH_SHORT).show()
                        stopMySelf()
                    }
        } else {
            stopMySelf()
        }
        NotificationManagerCompat.from(this).cancel(999)
        startForeground(222, notification.build())
        isServiceRunning = true
        for (listener in startStopListener) listener.onStartService(this)
    }
}
