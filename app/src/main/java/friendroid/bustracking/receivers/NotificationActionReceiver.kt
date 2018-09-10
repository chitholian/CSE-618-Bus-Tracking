package friendroid.bustracking.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationManagerCompat
import friendroid.bustracking.EXTRA_NOTIFICATION_ID

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        if (id != null && id > 0) {
            val manager: NotificationManagerCompat = NotificationManagerCompat.from(context!!)
            manager.cancel(id)
        }
    }
}
