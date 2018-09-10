package friendroid.bustracking

import android.content.Context
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

const val SPLASH_TIME: Long = 1500 // milliseconds
const val CHANEL_ID = "bm_channel"
const val EXTRA_NOTIFICATION_ID = "notification_id"
const val ACTION_HANDLE_PENDING_REQUEST = "friendroid.bustracking.ACTION_HANDLE_PENDING_REQUEST"
const val EXTRA_TEACHER_ID = "teacher_id"
const val EXTRA_RESULT = "result"
const val EXTRA_DELETED = "deleted"
const val EXTRA_REJECTED = "rejected"
const val EXTRA_POSITION = "position"
const val EXTRA_ACCEPTED = "accepted"

var cUser: FirebaseUser? = null
val cDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference

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
