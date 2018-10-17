package friendroid.bustracking.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import friendroid.bustracking.CHANEL_ID
import friendroid.bustracking.R
import friendroid.bustracking.SPLASH_TIME
import friendroid.bustracking.mUser

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        // create a notification chanel
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                    NotificationChannel(CHANEL_ID, getString(R.string.chanel_name), NotificationManager.IMPORTANCE_HIGH)
            )
        Handler().postDelayed({
            // Check user
            if (mUser.role.isNotEmpty()) {
                when (mUser.role) {
                    "admin" -> startActivity(Intent(baseContext, TransportControllerActivity::class.java))
                    "teacher" -> startActivity(Intent(baseContext, TeacherActivity::class.java))
                    "driver" -> startActivity(Intent(baseContext, BusDriverActivity::class.java))
                    else -> startActivity(Intent(this, LoginActivity::class.java))
                }
            } else startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME)
    }
}
