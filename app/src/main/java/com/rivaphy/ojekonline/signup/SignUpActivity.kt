package com.rivaphy.ojekonline.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.rivaphy.ojekonline.R
import com.rivaphy.ojekonline.login.LoginActivity
import com.rivaphy.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class SignUpActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        signUpButton.onClick {
            if (signUpEmail.text.isNotEmpty() &&
                signUpName.text.isNotEmpty() &&
                signUpHp.text.isNotEmpty() &&
                signUpPassword.text.isNotEmpty() &&
                signUpConfirmPassword.text.isNotEmpty()
            ) {
                //create function
                authUserSignUp(
                    signUpEmail.text.toString(),
                    signUpPassword.text.toString()
                )
            }
        }

    }

    private fun authUserSignUp(email: String, pass: String): Boolean? {

        auth = FirebaseAuth.getInstance()
        var status: Boolean? = null

        auth?.createUserWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (inserUser(
                        signUpName.text.toString(),
                        signUpEmail.text.toString(),
                        signUpHp.text.toString(),
                        task.result?.user!!
                    )
                ) {
                    startActivity<LoginActivity>()
                }
            } else {
                status = false
            }
        }
        return status
    }

    fun inserUser(
        name: String, email: String,
        hp: String, users: FirebaseUser
    ): Boolean {

        var user = Users()
        user.uid = users.uid
        user.name = name
        user.email = email
        user.hp = hp

        val database = FirebaseDatabase.getInstance()
        var key = database.reference.push().key
        val myRef = database.getReference(Constan.tb_Uaser)
        myRef.child(key!!).setValue(user)

        return true
    }
}
