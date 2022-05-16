package com.eronka.fincan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

lateinit var bottomNavigationView1: BottomNavigationView
var basketList = mutableListOf<datamodels.MenuItem>()
private lateinit var auth: FirebaseAuth

class UserProfileActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth= FirebaseAuth.getInstance()
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val auth = FirebaseAuth.getInstance()
        val user = auth?.currentUser
        findViewById<TextView>(R.id.profile_top_name_tv).text = user?.displayName.toString()
        findViewById<TextView>(R.id.profile_top_email_tv).text = user?.email.toString()
        findViewById<TextView>(R.id.profile_email).text = user?.email.toString()

        val args = intent.getBundleExtra("BUNDLE")
        basketList = (args!!.getSerializable("map") as MutableList<datamodels.MenuItem>?)!!
        if (basketList == null){
            basketList = mutableListOf()
        }
        bottomNavigationView1=findViewById(R.id.bottom_navigator)
        bottomNavigationView1.selectedItemId = 2131296678
        bottomNavigationView1.setOnItemSelectedListener {
            // homepage  2131296334
            // search    2131296339
            // basket    2131296736
            // profile   2131296678
            val args: Bundle = Bundle()
            if(it.itemId == 2131296339){
                val intent = Intent(this,SearchActivity::class.java)
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }else if(it.itemId == 2131296334){
                val intent = Intent(this,HomepageActivity::class.java)
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }
            else if(it.itemId == 2131296736){
                val intent = Intent(this,BasketActivity::class.java)
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }
            true
        }

    }

    fun logOut(view: View){
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}