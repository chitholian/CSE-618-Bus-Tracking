package friendroid.bustracking

import android.content.Context
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreSettings
import friendroid.bustracking.models.User

const val SPLASH_TIME: Long = 800 // milliseconds
const val CHANEL_ID = "bm_channel"
const val EXTRA_NOTIFICATION_ID = "notification_id"
const val ACTION_HANDLE_PENDING_REQUEST = "friendroid.bustracking.ACTION_HANDLE_PENDING_REQUEST"
const val ACTION_START_FOREGROUND = "friendroid.bustracking.ACTION_START_FOREGROUND"
const val ACTION_STOP_FOREGROUND = "friendroid.bustracking.ACTION_STOP_FOREGROUND"

const val EXTRA_TEACHER_ID = "teacher_id"
const val EXTRA_BUS_ID = "bus_id"
const val EXTRA_FIRST_READ = "first_read"

var cUser: FirebaseUser? = null
val fireSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true)
        .build()
var mUser = User()
fun alert(context: Context?, msg: String) {
    if (context != null)
        AlertDialog.Builder(context).setMessage(msg).setPositiveButton(context.getString(R.string.ok), null).create().show()
}

fun alert(context: Context?, res: Int) {
    if (context != null)
        alert(context, context.getString(res))
}


fun confirm(context: Context?, msg: String, ifNo: (() -> Unit)? = null, ifYes: (() -> Unit)? = null) {
    if (context != null)
        AlertDialog.Builder(context).setMessage(msg).setPositiveButton(context.getString(R.string.ok)) { d, _ ->
            d.dismiss()
            ifYes?.invoke()
        }.setNeutralButton(R.string.cancel) { d, _ ->
            d.cancel()
            ifNo?.invoke()
        }.setOnCancelListener { ifNo?.invoke() }.create().show()
}

fun confirm(context: Context?, res: Int, ifNo: (() -> Unit)? = null, ifYes: (() -> Unit)? = null) {
    if (context != null) {
        confirm(context, context.getString(res), ifNo, ifYes)
    }
}
