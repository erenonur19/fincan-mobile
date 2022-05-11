package com.eronka.fincan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth= Firebase.auth
        kullaniciKontrol()

    }
    fun kullaniciKontrol(){
        val user=auth.currentUser

        if(user!=null){
            val intent=Intent(this,HomepageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun login(view:View){
        val intent= Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
    fun register(view:View){
        val intent= Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }
}