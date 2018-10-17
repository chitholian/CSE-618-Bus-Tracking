package friendroid.bustracking.activities

import android.os.Bundle
import friendroid.bustracking.R

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginUser()
    }
}
