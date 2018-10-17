package friendroid.bustracking.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.Toast
import friendroid.bustracking.*
import friendroid.bustracking.activities.BusDriverActivity
import friendroid.bustracking.services.LocationUpdaterService

class DeviceStateListener : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                val state = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                val info = state?.activeNetworkInfo
                if (info == null || !info.isConnected) showError(context)
            }
            LocationManager.PROVIDERS_CHANGED_ACTION -> {
                val manager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                manager ?: return
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // all ok
                    System.err.println("Providers are : "+manager.allProviders)
                } else showError(context)
            }
        }
    }

    private fun showError(context: Context?) {
        // show a notification saying that the service has stopped due to provider was disabled.
        context ?: return
        val notification = NotificationCompat.Builder(context, CHANEL_ID)
                .setContentTitle(context.getString(R.string.service_stopped))
                .setContentIntent(PendingIntent.getActivity(context, 999,
                        Intent(context, BusDriverActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            action = Intent.ACTION_MAIN
                        }, 0))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentText(context.getString(R.string.turn_on_location))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        NotificationManagerCompat.from(context).apply {
            cancel(222)
            notify(999, notification.build())
        }
        Toast.makeText(context, R.string.turn_on_location, Toast.LENGTH_SHORT).show()
        stopService(context)
    }

    private fun stopService(context: Context) {
        val intent = Intent(context, LocationUpdaterService::class.java)
        intent.action = ACTION_STOP_FOREGROUND
        context.startService(intent)
    }
}
