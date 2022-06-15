package com.eronka.fincan

import adapters.RecyclerMenuItemAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import datamodels.CafeItem
import datamodels.MenuItem
import interfaces.ItemApi
import services.FirebaseDBService
import java.io.Serializable
import java.util.*

class ShowMenuActivity : AppCompatActivity(),  RecyclerMenuItemAdapter.OnItemClickListener, ItemApi {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private var allItems = ArrayList<MenuItem>()
    private var tempItems = ArrayList<MenuItem>()
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var recyclerItemAdapter: RecyclerMenuItemAdapter
    private lateinit var showAllSwitch: SwitchCompat
    var basketList = mutableListOf<MenuItem>()
    lateinit var cafe : CafeItem
    @SuppressLint("ResourceType")


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_menu)
        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
        val args = intent.getBundleExtra("BUNDLE")
        basketList = (args!!.getSerializable("map") as MutableList<MenuItem>?)!!
        if (basketList == null){
            basketList = mutableListOf()
        }
        cafe = (intent.getSerializableExtra("cafe") as? CafeItem)!!
        cafeInfoLoader(cafe)
        loadItems()
        bottomNavigationView1=findViewById(R.id.bottom_navigator)
        bottomNavigationView1.selectedItemId = R.id.anasayfa
        bottomNavigationView1.setOnItemSelectedListener {
            if(it.itemId == R.id.arama){
                val intent = Intent(this,SearchActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }else if(it.itemId == R.id.anasayfa){
                val intent = Intent(this,HomepageActivity::class.java)
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

        //Toast.makeText(this, bundle?.get(3)?.toString(),Toast.LENGTH_LONG).show()

    }

    override fun onBackPressed() {
        val intent = Intent(this,HomepageActivity::class.java)
        val args: Bundle = Bundle()
        args.putSerializable("map", basketList as Serializable)
        intent.putExtra("BUNDLE", args)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
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
            this,
            basketList
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
        progressDialog.setMessage("For fast and smooth experience, please wait menu to be downloaded.")
        progressDialog.create()
        progressDialog.show()

        FirebaseDBService().readAllItem(this)
    }

    override fun onFetchSuccessListener(list: ArrayList<MenuItem>) {
        for (item in list) {
            if (item.cafeKey.equals(intent.getStringExtra("cafekey"))){
                allItems.add(item)
            }
        }
        tempItems.addAll(allItems)

        recyclerItemAdapter.notifyItemRangeInserted(0, tempItems.size)

        progressDialog.dismiss()
    }

    override fun onItemClick(item: MenuItem) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onPlusBtnClick(item: MenuItem) {
        if (basketList.isEmpty()){
            if (item.quantity == 0){
                item.quantity = 1
                basketList.add(item)
            }else{
                item.quantity += 1
            }
        }else{
            if(basketList[0].cafeKey == item.cafeKey){
                var bool = false
                basketList.forEach(){
                    if(it.itemName == item.itemName){
                        it.quantity += 1
                        item.quantity = it.quantity
                        bool = true
                    }
                }
                if (!bool){
                    item.quantity = 1
                    basketList.add(item)
                }

            }else{
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_alert)
                    .setTitle("Alert!")
                    .setMessage("You have already have item in your basket from another cafe.\nDo you want to clear your basket ?")
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                        basketList.clear()
                        item.quantity = 1
                        basketList.add(item)
                        itemRecyclerView.adapter!!.notifyDataSetChanged()
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    })
                    .create().show()
            }
        }

        itemRecyclerView.adapter!!.notifyDataSetChanged()
        //Toast.makeText(this,"plus " + item.itemName,Toast.LENGTH_SHORT).show()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMinusBtnClick(item: MenuItem) {
        if (item.quantity != 0){
            item.quantity -= 1
            if(item.quantity == 0){
                basketList.remove(item)
            }
        }
        itemRecyclerView.adapter!!.notifyDataSetChanged()

    }

    fun cafeInfoLoader(cafeItem: CafeItem){
        val cafeImage: ImageView = findViewById(R.id.item_image)
        val cafeName: TextView = findViewById(R.id.item_name)
        val cafeStars: TextView = findViewById(R.id.item_stars)
        val cafeAddress: TextView = findViewById(R.id.list_cafes_item)

        if (cafeItem.imageUrl != null && cafeItem.imageUrl != ""){
            Picasso.get().load(cafeItem.imageUrl).into(cafeImage)
        }
        cafeName.text = cafeItem.cafeName
        cafeStars.text = cafeItem.cafeStars.toString()
        cafeAddress.text = cafeItem.cafeAddress
    }
}