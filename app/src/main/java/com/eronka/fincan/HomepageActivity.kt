package com.eronka.fincan

import adapters.RecyclerCafeItemAdapter
import services.FirebaseDBService
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import datamodels.CafeItem
import interfaces.CafeApi
import interfaces.RequestType
import kotlinx.android.synthetic.main.activity_show_menu.*


class HomepageActivity : AppCompatActivity(),  RecyclerCafeItemAdapter.OnItemClickListener, CafeApi {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
    private var allItems = ArrayList<CafeItem>()
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var recyclerFoodAdapter: RecyclerCafeItemAdapter
    private lateinit var showAllSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
        loadMenu()
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
    private fun loadMenu() {
        val sharedPref: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        itemRecyclerView = findViewById(R.id.cafes_recycler_view)
        recyclerFoodAdapter = RecyclerCafeItemAdapter(
            applicationContext,
            allItems,
            sharedPref.getInt("loadItemImages", 0),
            this
        )
        itemRecyclerView.adapter = recyclerFoodAdapter
        itemRecyclerView.layoutManager = LinearLayoutManager(this@HomepageActivity)
        recyclerFoodAdapter.filterList(allItems) //display complete list
        loadOnlineMenu()

    }

    private fun loadOnlineMenu() {
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Loading Menu...")
        progressDialog.setMessage("For fast and smooth experience, you can download Menu for Offline.")
        progressDialog.create()
        progressDialog.show()

        FirebaseDBService().readAllMenu(this, RequestType.READ)
    }

    override fun onItemClick(item: CafeItem) {

    }

    override fun onFetchSuccessListener(list: ArrayList<CafeItem>, requestType: RequestType) {
        if (requestType == RequestType.READ) {
            for (item in list) {
                println(item.email)
                allItems.add(item)
            }
            recyclerFoodAdapter.notifyItemRangeInserted(0, allItems.size)
        }

        progressDialog.dismiss()
    }
}