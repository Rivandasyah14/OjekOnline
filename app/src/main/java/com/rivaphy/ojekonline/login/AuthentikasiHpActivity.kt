package com.rivaphy.ojekonline.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.rivaphy.ojekonline.MainActivity
import com.rivaphy.ojekonline.R
import com.rivaphy.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.activity_authentikasi_hp.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class AuthentikasiHpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentikasi_hp)

        val key = intent.getStringExtra(Constan.Key)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_Uaser)

        //update realtime database
        authentikasisubmit.onClick {
            if (authentikasinomerhp.text.toString().isNotEmpty()) {
                myRef.child(key).child("hp")
                    .setValue(authentikasinomerhp.text.toString())
                startActivity<MainActivity>()
            } else toast("tidak boleh kosong")
        }
    }
}
