package com.rivaphy.ojekonline.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rivaphy.ojekonline.MainActivity
import com.rivaphy.ojekonline.R
import com.rivaphy.ojekonline.signup.SignUpActivity
import com.rivaphy.ojekonline.signup.Users
import com.rivaphy.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    var googleSignInClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        signUpButtonGmail.onClick {
            signIn()
        }

        signUpLink.onClick {
            startActivity<SignUpActivity>()
        }

        loginButton.onClick {
            if (loginName.text.isNotEmpty() &&
                loginPassword.text.isNotEmpty()
            ) {
                authUserSignIn(
                    loginName.text.toString(),
                    loginPassword.text.toString()
                )
            }
        }
    }

    //authentikasi sign in
    private fun authUserSignIn(email: String, pass: String) {
        var status: Boolean? = null

        auth?.signInWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    startActivity<MainActivity>()
                    finish()
                } else {
                    toast("login failed")
                }
            }
    }

    //request sign in email
    private fun signIn() {
        val gson = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gson)

        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 4)
    }

    //hasil request sign in google
    /* setelah profile memilih account yang sudah ter sign in akan
    mengambil informasi dari profile yang sign ini google menggunakna
    onActivityResult */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 4) {
            val task = GoogleSignIn
                .getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {

            }
        }
    }

    //sign in ke firebase, authentication firebase
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {

        var uid = String()
        val credential1 = GoogleAuthProvider
            .getCredential(acct?.idToken, null)

        auth?.signInWithCredential(credential1)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth?.currentUser
                    checkDatabase(task.result?.user?.uid, acct)
                    uid = user?.uid.toString()
                } else {

                }
            }

    }

    //check database
    //apakah profile yang sign in udah di realtime database atau belum
    private fun checkDatabase(uid: String?, acct: GoogleSignInAccount?) {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_Uaser)
        val query = myRef.orderByChild("uid").equalTo(auth?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            //kalau udah ada dia bakalan begini
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    startActivity<MainActivity>()
                } else {
                    acct?.displayName?.let {
                        acct.email?.let { it1 ->
                            insertUser(it, it1, "08", uid)
                        }
                    }
                }
            }
        })
    }

    /* proses ini hampir sama dengan sign up, kalo proses ini berhasil akan
    pindah ke authentikasi activity untuk memasukan dari nomer telepon profile */
    private fun insertUser(name: String, email: String, hp: String, idUser: String?): Boolean {

        val user = Users()
        user.email = email
        user.name = name
        user.hp = hp
        user.uid = auth?.uid

        val database = FirebaseDatabase.getInstance()
        val key = database.reference.push().key
        val myRef = database.getReference(Constan.tb_Uaser)

        myRef.child(key ?: "").setValue(user)
        startActivity<AuthentikasiHpActivity>(Constan.Key to key)

        return true
    }
}
