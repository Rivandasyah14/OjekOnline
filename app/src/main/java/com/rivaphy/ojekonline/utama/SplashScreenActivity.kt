package com.rivaphy.ojekonline.utama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.rivaphy.ojekonline.MainActivity
import com.rivaphy.ojekonline.R
import com.rivaphy.ojekonline.login.LoginActivity
import org.jetbrains.anko.startActivity

class SplashScreenActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()
        Handler().postDelayed(Runnable {
            if (auth?.currentUser?.displayName != null) {
                startActivity<MainActivity>()
            }
            else startActivity<LoginActivity>()
        }, 3000)
    }
}
