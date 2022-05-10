package com.eronka.fincan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Restaurant(val username: String? = null, val email: String? = null) {

}

class HomepageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
        emailGoster()
    }

    fun emailGoster(){
        //val user = User("bedo","bedo@bedo")
        //database.child("restaurants").child("bedo").setValue(user)
        val kullanici_mail = auth.currentUser?.email.toString()
        auth.currentUser?.sendEmailVerification()
        val kullaniciID=auth.currentUser?.email.toString()
        Toast.makeText(this,"${kullanici_mail} li kullanıcı giriş yapmıştır", Toast.LENGTH_LONG).show()
    }
    fun logOut(view:View){
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}