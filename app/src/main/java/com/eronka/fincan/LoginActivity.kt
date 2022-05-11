package com.eronka.fincan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth= FirebaseAuth.getInstance()
    }

    fun login(view: View){
        val email=login_email_txt.text.toString()
        val password=login_password_txt.text.toString()
        if(email!=""&&password!=""&&password.length>=6){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val intent= Intent(this,HomepageActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this,"Lütfen Bilgilerinizi Eksiksiz Giriniz..",Toast.LENGTH_LONG).show()
        }

    }
    fun geriDon(view:View){
        finish()
    }
    fun goRegister(view:View){
        startActivity(Intent(this,RegisterActivity::class.java))
        finish()
    }

}