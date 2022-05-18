package com.eronka.fincan

import adapters.RecyclerBasketItemAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class BasketActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, RecyclerBasketItemAdapter.OnItemClickListener  {

    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerBasketItemAdapter
    var basketList = mutableListOf<datamodels.MenuItem>()

    private lateinit var totalItemsTV: TextView
    private lateinit var totalPriceTV: TextView
    private lateinit var totalTaxTV: TextView
    private lateinit var subTotalTV: TextView
    private lateinit var proceedToPayBtn: Button
    private lateinit var orderTakeAwayTV: TextView

    private var totalPrice: Float = 0F
    private var totalItems: Int = 0
    private var totalTax: Float = 0F
    private var subTotal: Float = 0F

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_alert)
            .setTitle("Alert!")
            .setMessage("Do you want to cancel your order?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                super.onBackPressed()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            .create().show()
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        val sharedPref: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        itemRecyclerView = findViewById(R.id.order_recycler_view)
        val args = intent.getBundleExtra("BUNDLE")
        basketList = (args!!.getSerializable("map") as MutableList<datamodels.MenuItem>?)!!
        if (basketList == null){
            basketList = mutableListOf()
        }
        recyclerAdapter = RecyclerBasketItemAdapter(
            applicationContext,
            basketList,
            sharedPref.getInt("loadItemImages", 0),
            this
        )


        bottomNavigationView1=findViewById(R.id.bottom_navigator)
        bottomNavigationView1.selectedItemId = R.id.sepet
        bottomNavigationView1.setOnItemSelectedListener {
            // homepage  2131296334
            // search    2131296339
            // basket    2131296736
            // profile   2131296678
            if(it.itemId== R.id.anasayfa){
                val intent = Intent(this,HomepageActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }else if(it.itemId == R.id.arama){
                val intent = Intent(this,SearchActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }
            else if(it.itemId == R.id.profile){
                val intent = Intent(this,UserProfileActivity::class.java)
                val args: Bundle = Bundle()
                args.putSerializable("map", basketList as Serializable)
                intent.putExtra("BUNDLE", args)
                startActivity(intent)
                finish()
            }
            true
        }

        totalItemsTV = findViewById(R.id.order_total_items_tv)
        totalPriceTV = findViewById(R.id.order_total_price_tv)
        totalTaxTV = findViewById(R.id.order_total_tax_tv)
        subTotalTV = findViewById(R.id.order_sub_total_tv)
        proceedToPayBtn = findViewById(R.id.proceed_to_pay_btn)
        orderTakeAwayTV = findViewById(R.id.order_take_away_time_tv)

        totalPrice = 0F
        totalItems = 0
        totalTax = totalPrice * 0.12F

        var c : Calendar = Calendar.getInstance()
        var df : SimpleDateFormat? = null
        var formattedDate = ""

        // goes to main method or onCreate(Android)
        df = SimpleDateFormat("hh:mm a")
        formattedDate = df!!.format(c.time)
        orderTakeAwayTV.text = formattedDate
        loadItems()

    }
    @SuppressLint("SetTextI18n")
    private fun loadItems() {
        val sharedPref: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        itemRecyclerView = findViewById(R.id.order_recycler_view)
        recyclerAdapter = RecyclerBasketItemAdapter(
            applicationContext,
            basketList,
            sharedPref.getInt("loadItemImages", 0),
            this
        )
        itemRecyclerView.adapter = recyclerAdapter
        itemRecyclerView.layoutManager = LinearLayoutManager(this@BasketActivity)
        recyclerAdapter.filterList(basketList) //display complete list

        totalPrice = 0F
        totalItems = 0
        basketList.forEach {
            totalPrice += it.itemPrice * it.quantity
            if(it.quantity != 0){
                totalItems++
            }
        }
        totalTax = ((totalPrice * 0.12F)* 100.0F).roundToInt() / 100.0F
        totalItemsTV.text = totalItems.toString()
        totalPriceTV.text = (((totalPrice* 100.0F).roundToInt() / 100.0F)).toString() + "₺"
        totalTaxTV.text = (((totalTax* 100.0F).roundToInt() / 100.0F)).toString() + "₺"
        subTotalTV.text = (((totalPrice + totalTax)* 100.0F).roundToInt() / 100.0F).toString() + "₺"
        subTotal = totalPrice + totalTax
        if(totalItems == 0){
            totalPriceTV.text = "-"
            totalTaxTV.text = "-"
            subTotalTV.text = "-"
        }
    }

    fun goBack(view: View){
        finish()
    }
    fun changeOrderTakeAwayTime(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, this, hour, minute, true)
        timePickerDialog.show()
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        val time = "$hourOfDay:$minute"
        val f24Hours = SimpleDateFormat("HH:mm")
        try {
            val date = f24Hours.parse(time)
            val f12Hours = SimpleDateFormat("hh:mm aa")
            orderTakeAwayTV.text = f12Hours.format(date)
        } catch (e: Exception) {}
    }
    fun contactUs(view: View){
        val intent = Intent(this,ContactUsActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClick(item: datamodels.MenuItem) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onPlusBtnClick(item: datamodels.MenuItem) {
        if (item.quantity == 0){
            item.quantity = 1
        }else{
            item.quantity += 1
        }
        loadItems()

        //Toast.makeText(this,"plus " + item.itemName,Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMinusBtnClick(item: datamodels.MenuItem) {
        if (item.quantity != 0){
            item.quantity -= 1
        }else{
            basketList.remove(item)
        }
        loadItems()

    }
    fun openPaymentActivity(view: View) {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("totalItemPrice", totalPrice)
        intent.putExtra("totalTaxPrice", totalTax)
        intent.putExtra("subTotalPrice", subTotal)
        intent.putExtra("takeAwayTime", orderTakeAwayTV.text.toString())
        val args: Bundle = Bundle()
        args.putSerializable("map", basketList as Serializable)
        intent.putExtra("BUNDLE", args)
        startActivity(intent)

    }
}