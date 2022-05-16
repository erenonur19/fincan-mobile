package com.eronka.fincan

import adapters.RecyclerCafeItemAdapter
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import datamodels.CafeItem
import datamodels.MenuItem
import interfaces.CafeApi
import kotlinx.android.synthetic.main.activity_homepage.*
import services.FirebaseDBService
import java.util.*
import java.io.*


class HomepageActivity : AppCompatActivity(),  RecyclerCafeItemAdapter.OnItemClickListener, CafeApi {


    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private var allItems = ArrayList<CafeItem>()
    private var tempItems = ArrayList<CafeItem>()
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var recyclerFoodAdapter: RecyclerCafeItemAdapter
    private lateinit var showAllSwitch: SwitchCompat
    lateinit var bottomNavigationView1: BottomNavigationView
    var basketList = mutableListOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        val args = intent.getBundleExtra("BUNDLE")

        if (args != null){
            basketList = (args!!.getSerializable("map") as ArrayList<MenuItem>?)!!
        }
        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
        loadMenu()

        bottomNavigationView1=findViewById(R.id.bottom_navigator)
        bottomNavigationView1.setOnItemSelectedListener {
            if(it.itemId == R.id.arama){
                val intent = Intent(this,SearchActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }
            else if(it.itemId == R.id.sepet){
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                tempItems.clear()
                val searchText = p0!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    allItems.forEach {
                        if(it.cafeName.lowercase(Locale.getDefault()).contains(searchText)){
                            tempItems.add(it)
                        }else if(it.cafeAddress.lowercase(Locale.getDefault()).contains(searchText)){
                            tempItems.add(it)
                        }
                    }
                    itemRecyclerView.adapter!!.notifyDataSetChanged()
                }else{
                    tempItems.clear()
                    tempItems.addAll(allItems)
                    itemRecyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun logOut(view:View){
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun loadMenu() {
        val sharedPref: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        itemRecyclerView = findViewById(R.id.cafes_recycler_view)
        recyclerFoodAdapter = RecyclerCafeItemAdapter(
            applicationContext,
            tempItems,
            sharedPref.getInt("loadItemImages", 0),
            this
        )
        itemRecyclerView.adapter = recyclerFoodAdapter
        itemRecyclerView.layoutManager = LinearLayoutManager(this@HomepageActivity)
        recyclerFoodAdapter.filterList(tempItems) //display complete list

        loadOnlineMenu()
    }

    private fun loadOnlineMenu() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading Cafes...")
        progressDialog.setMessage("For fast and smooth experience, please wait cafes to be downloaded.")
        progressDialog.create()
        progressDialog.show()

        FirebaseDBService().readAllCafe(this)
    }

    override fun onItemClick(item: CafeItem) {
        val intent = Intent(this,ShowMenuActivity::class.java)
        intent.putExtra("cafekey",item.cafeKey)

        val args: Bundle = Bundle()
        args.putSerializable("map", basketList as Serializable)
        intent.putExtra("BUNDLE", args)
        intent.putExtra("cafe",item)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    override fun onFetchSuccessListener(list: ArrayList<CafeItem>) {
        for (item in list) {
            allItems.add(item)
        }
        tempItems.addAll(allItems)
        recyclerFoodAdapter.notifyItemRangeInserted(0, tempItems.size)

        progressDialog.dismiss()
        restaurant_sayisi.text= "${tempItems.size} cafe listed!"
    }
}