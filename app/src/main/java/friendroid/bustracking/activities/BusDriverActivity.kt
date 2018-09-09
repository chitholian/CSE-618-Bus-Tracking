package friendroid.bustracking.activities

import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.receivers.NotificationActionReceiver
import friendroid.bustracking.utils.CHANEL_ID
import friendroid.bustracking.utils.EXTRA_NOTIFICATION_ID
import kotlinx.android.synthetic.main.activity_bus_driver.*
import kotlinx.android.synthetic.main.activity_home.*


class BusDriverActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        waiting.setOnClickListener { it ->
            it.visibility = View.GONE
            fragment_container.addView(View.inflate(this, R.layout.activity_bus_driver, null))

            button_broadcast_msg.setOnClickListener {
                val msg = message_field.text.trim()
                if (msg.isEmpty()) {
                    message_field.error = getString(R.string.enter_msg)
                    message_field.requestFocus()
                } else {
                    it.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                    delayed {
                        toast(getString(R.string.broadcast_sent))

                        // show demo notification.
                        val i = Intent(this, NotificationActionReceiver::class.java)
                        i.putExtra(EXTRA_NOTIFICATION_ID, 1)
                        val builder = NotificationCompat.Builder(this, CHANEL_ID)
                                .setSmallIcon(android.R.drawable.ic_dialog_email)
                                .setContentTitle("Bus Number One")
                                .setContentText(msg)
                                .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .addAction(android.R.drawable.ic_notification_clear_all, getString(R.string.dismiss),
                                        PendingIntent.getBroadcast(this, 0, i, 0))
                        val manager: NotificationManagerCompat = NotificationManagerCompat.from(this)

                        manager.notify(1, builder.build())
                        progressBar.visibility = View.INVISIBLE
                        it.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bus_driver, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> logout()
            R.id.menu_about -> showAbout()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        confirmBeforeExit()
    }

}
