package com.eronka.fincan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import datamodels.MenuItem
import java.io.Serializable

class SearchActivity : AppCompatActivity() {
    var basketList = mutableListOf<MenuItem>()
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val args = intent.getBundleExtra("BUNDLE")
        basketList = (args!!.getSerializable("map") as MutableList<MenuItem>?)!!
        if (basketList == null){
            basketList = mutableListOf()
        }
        bottomNavigationView1=findViewById(R.id.bottom_navigator)
        bottomNavigationView1.selectedItemId = 2131296339
        bottomNavigationView1.setOnItemSelectedListener {
            // homepage  2131296334
            // search    2131296339
            // basket    2131296736
            // profile   2131296678
            if(it.itemId==2131296334){
                val intent = Intent(this,HomepageActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            } else if(it.itemId == 2131296736){
                val intent = Intent(this,BasketActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }else if(it.itemId == 2131296678){
                val intent = Intent(this,UserProfileActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }
            true
        }
    }
}