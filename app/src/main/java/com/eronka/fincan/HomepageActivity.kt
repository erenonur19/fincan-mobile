package com.eronka.fincan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class HomepageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        auth= FirebaseAuth.getInstance()
        emailGoster()

    }

    fun emailGoster(){
        val kullanici_mail=auth.currentUser?.uid.toString()
        Toast.makeText(this,"${kullanici_mail} li kullanıcı giriş yapmıştır", Toast.LENGTH_LONG).show()
    }
    fun logOut(view:View){
        auth.signOut()
        val intent= Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}