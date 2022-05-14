package com.eronka.fincan

import adapters.RecyclerCafeItemAdapter
import adapters.RecyclerMenuItemAdapter
import android.app.ProgressDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import datamodels.MenuItem
import interfaces.ItemApi
import services.FirebaseDBService
import java.util.*
import kotlin.collections.ArrayList

class ShowMenuActivity : AppCompatActivity(),  RecyclerMenuItemAdapter.OnItemClickListener, ItemApi {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private var allItems = ArrayList<MenuItem>()
    private var tempItems = ArrayList<MenuItem>()
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var recyclerItemAdapter: RecyclerMenuItemAdapter
    private lateinit var showAllSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_menu)
        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
        loadItems()
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
                        if(it.itemName.lowercase(Locale.getDefault()).contains(searchText)){
                            tempItems.add(it)
                        }else if(it.itemCategory.lowercase(Locale.getDefault()).contains(searchText)){
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



    private fun loadItems() {
        val sharedPref: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        itemRecyclerView = findViewById(R.id.items_recycler_view)
        recyclerItemAdapter = RecyclerMenuItemAdapter(
            applicationContext,
            tempItems,
            sharedPref.getInt("loadItemImages", 0),
            this
        )
        itemRecyclerView.adapter = recyclerItemAdapter
        itemRecyclerView.layoutManager = LinearLayoutManager(this@ShowMenuActivity)
        recyclerItemAdapter.filterList(tempItems) //display complete list
        loadOnlineItems()

    }

    private fun loadOnlineItems() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading Menu...")
        progressDialog.setMessage("For fast and smooth experience, you can download Menu for Offline.")
        progressDialog.create()
        progressDialog.show()

        FirebaseDBService().readAllItem(this)
    }

    override fun onFetchSuccessListener(list: ArrayList<MenuItem>) {
        for (item in list) {
            println(intent.getStringExtra("cafekey"))
            if (item.cafeKey.equals(intent.getStringExtra("cafekey"))){

                allItems.add(item)
            }
        }
        tempItems.addAll(allItems)

        recyclerItemAdapter.notifyItemRangeInserted(0, tempItems.size)

        progressDialog.dismiss()
    }

    override fun onItemClick(item: MenuItem) {
        Toast.makeText(this,"${item.cafeKey}",Toast.LENGTH_LONG).show()
    }

    override fun onPlusBtnClick(item: MenuItem) {
        Toast.makeText(this,"plus " + item.itemName,Toast.LENGTH_SHORT).show()

    }

    override fun onMinusBtnClick(item: MenuItem) {
        Toast.makeText(this,"minus " + item.itemName,Toast.LENGTH_SHORT).show()
    }


}