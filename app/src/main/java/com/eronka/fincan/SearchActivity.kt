package com.eronka.fincan

import adapters.RecyclerOrderItemAdapter
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import datamodels.MenuItem
import datamodels.OrderItem
import interfaces.OrderApi

import services.FirebaseDBService
import java.io.Serializable
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), OrderApi {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private var allItems = ArrayList<OrderItem>()
    private var tempItems = ArrayList<OrderItem>()
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var recyclerFoodAdapter: RecyclerOrderItemAdapter
    lateinit var bottomNavigationView1: BottomNavigationView
    var basketList = mutableListOf<MenuItem>()
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()
        val args = intent.getBundleExtra("BUNDLE")
        basketList = (args!!.getSerializable("map") as MutableList<MenuItem>?)!!
        if (basketList == null){
            basketList = mutableListOf()
        }

        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
        loadMenu()

        bottomNavigationView1=findViewById(R.id.bottom_navigator)
        bottomNavigationView1.selectedItemId = R.id.arama
        bottomNavigationView1.setOnItemSelectedListener {
            if(it.itemId==R.id.anasayfa){
                val intent = Intent(this,HomepageActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            } else if(it.itemId == R.id.sepet){
                val intent = Intent(this,BasketActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }else if(it.itemId == R.id.profile){
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

    private fun loadMenu() {
        val sharedPref: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        orderRecyclerView = findViewById(R.id.orders_recycler_view)
        recyclerFoodAdapter = RecyclerOrderItemAdapter(
            this,
            tempItems,
            sharedPref.getInt("loadItemImages", 0),
        )
        orderRecyclerView.adapter = recyclerFoodAdapter
        orderRecyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
        recyclerFoodAdapter.filterList(tempItems) //display complete list

        loadOnlineMenu()
    }

    private fun loadOnlineMenu() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading Orders...")
        progressDialog.setMessage("For fast and smooth experience, please wait cafes to be downloaded.")
        progressDialog.create()
        progressDialog.show()

        FirebaseDBService().readAllOrder(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onFetchSuccessListener(list: ArrayList<OrderItem>) {
        for (item in list) {
            allItems.add(item)
        }
        tempItems.addAll(allItems)
        recyclerFoodAdapter.notifyItemRangeInserted(0, tempItems.size)

        progressDialog.dismiss()
    }
}