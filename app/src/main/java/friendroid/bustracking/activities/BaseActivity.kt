package friendroid.bustracking.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import friendroid.bustracking.R
import friendroid.bustracking.confirm

abstract class BaseActivity : AppCompatActivity() {
    private var lastBackPressedAt = 0L
    private val LOGIN_REQ_CODE = 111
    private var t: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        t = Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT)
    }


    fun toast(msg: String, time: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, time).show()
    }

    fun toast(res: Int, time: Int = Toast.LENGTH_SHORT) {
        toast(getString(res), time)
    }

    fun logout() {
        confirm(this, R.string.confirm_logout) {
            AuthUI.getInstance().signOut(this)
            finish()
        }
    }

    protected fun confirmBeforeExit() {
        val now = System.currentTimeMillis()
        if (now - lastBackPressedAt <= 1000) {
            t?.cancel()
            super.onBackPressed()
        } else {
            t?.show()
            lastBackPressedAt = now
        }
    }

    fun showAbout() {
        AlertDialog.Builder(this).setTitle(R.string.about).setMessage(R.string.about_app).setPositiveButton(R.string.ok, null).create().show()
    }

    fun loginUser() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(listOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.FacebookBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.PhoneBuilder().build())).build(), LOGIN_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQ_CODE) {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
        /*if (resultCode == Activity.RESULT_OK) {
            cUser = FirebaseAuth.getInstance().currentUser
            if (cUser != null) {
                cDatabaseRef.child("users").child(cUser?.uid!!).child("role").addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                when (p0.getValue(String::class.java)) {
                                    "controller" -> startActivity(Intent(baseContext, TransportControllerActivity::class.java))
                                    "teacher" -> startActivity(Intent(baseContext, TeacherActivity::class.java))
                                    "driver" -> startActivity(Intent(baseContext, BusDriverActivity::class.java))
                                    else -> startActivity(Intent(baseContext, RegistrationActivity::class.java))
                                }
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                confirm(baseContext, "${p0.message}, ${getString(R.string.retry)} ?", { finish() }, { loginUser() })
                            }
                        })
            } else {
                toast(R.string.login_failed)
                loginUser()
            }
        } else {
            confirm(this, "${getString(R.string.login_failed)}, Error Code = $resultCode; ${getString(R.string.retry)} ?", { finish() }, {
                //                    loginUser()
                startActivity(Intent(baseContext, RegistrationActivity::class.java))
                finish()
            })
        }*/
    }

    fun delayed(ms: Long = 1000, task: () -> Unit) {
        Handler().postDelayed({
            task.invoke()
        }, ms)
    }
}
