package com.eronka.fincan

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class PaymentActivity : AppCompatActivity() {

    private lateinit var totalPaymentTV: TextView
    private lateinit var confirmPaymentBtn: Button
    private lateinit var paymentWalletBtn: Button
    private lateinit var paymentCreditDebitBtn: Button
    private lateinit var paymentBhimUpiBtn: Button

    private lateinit var cashPaymentRB: RadioButton
    private lateinit var walletsRB: RadioButton
    private lateinit var savedCardRB: RadioButton
    private lateinit var creditDebitRB: RadioButton
    private lateinit var bhimUpiRB: RadioButton
    private lateinit var netBankingRB: RadioButton

    private lateinit var walletSection: LinearLayout
    private lateinit var creditDebitSection: LinearLayout
    private lateinit var bhimUpiSection: LinearLayout

    private lateinit var allWalletsLL: LinearLayout

    var totalItemPrice = 0.0F
    var totalTaxPrice = 0.0F
    var subTotalPrice = 0.0F

    var takeAwayTime = ""

    private var selectedWallet = ""
    private var selectedSavedCard = ""
    private var enteredCreditDebitCard = ""
    private var enteredUPI = ""

    private lateinit var savedCardRecyclerView: RecyclerView

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_alert)
            .setTitle("Alert!")
            .setMessage("Do you want to cancel the payment?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                super.onBackPressed()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            .create().show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_payment)
        supportActionBar?.hide()
        totalItemPrice = intent.getFloatExtra("totalItemPrice", 0.0F)
        totalTaxPrice = intent.getFloatExtra("totalTaxPrice", 0.0F)
        subTotalPrice = intent.getFloatExtra("subTotalPrice", 0.0F)

        takeAwayTime = intent?.getStringExtra("takeAwayTime").toString()

        totalPaymentTV = findViewById(R.id.total_payment_tv)
        totalPaymentTV.text = "\$%.2f".format(subTotalPrice)

        cashPaymentRB = findViewById(R.id.cash_payment_radio_btn)
        walletsRB = findViewById(R.id.wallets_radio_btn)
        savedCardRB = findViewById(R.id.saved_cards_radio_btn)
        creditDebitRB = findViewById(R.id.credit_debit_card_radio_btn)
        bhimUpiRB = findViewById(R.id.bhim_upi_radio_btn)
        netBankingRB = findViewById(R.id.net_banking_radio_btn)

        walletSection = findViewById(R.id.wallets_section_ll)
        creditDebitSection = findViewById(R.id.credit_debit_section_ll)
        bhimUpiSection = findViewById(R.id.bhim_upi_section_ll)

        setupPaymentButtons()
        setupWallets()

        savedCardRecyclerView = findViewById(R.id.payment_saved_cards_recycler_view)

        savedCardRecyclerView.layoutManager = LinearLayoutManager(this@PaymentActivity)

        findViewById<ImageView>(R.id.payment_go_back_iv).setOnClickListener { onBackPressed() }
    }

    private fun setupWallets() {
        allWalletsLL = findViewById(R.id.wallets_ll)
        allWalletsLL.getChildAt(0).setOnClickListener {setClickedWallet(0)}
        allWalletsLL.getChildAt(1).setOnClickListener {setClickedWallet(1)}
        allWalletsLL.getChildAt(2).setOnClickListener {setClickedWallet(2)}
        allWalletsLL.getChildAt(3).setOnClickListener {setClickedWallet(3)}
        allWalletsLL.getChildAt(4).setOnClickListener {setClickedWallet(4)}
        setClickedWallet(0) //by default selected wallet
    }

    private fun setClickedWallet(selectedWalletPos: Int) {
        for(i in 0..4) {
            allWalletsLL.getChildAt(i).setBackgroundDrawable(resources.getDrawable(R.drawable.border_unselected_option))
        }
        allWalletsLL.getChildAt(selectedWalletPos).setBackgroundDrawable(resources.getDrawable(R.drawable.border_selected_option))

        when(selectedWalletPos) {
            0 -> selectedWallet = "PayTM"
            1 -> selectedWallet = "Google Pay"
            2 -> selectedWallet = "PhonePe"
            3 -> selectedWallet = "Amazon Pay"
            4 -> selectedWallet = "Jio Money"
        }
    }

    private fun setupPaymentButtons() {
        confirmPaymentBtn = findViewById(R.id.confirm_payment_btn)
        paymentWalletBtn = findViewById(R.id.payment_wallet_btn)
        paymentCreditDebitBtn = findViewById(R.id.payment_credit_debit_card_btn)
        paymentBhimUpiBtn = findViewById(R.id.payment_bhim_upi_btn)

        paymentWalletBtn.text = "Pay Securely \$%.2f".format(subTotalPrice)
        paymentCreditDebitBtn.text = "Pay \$%.2f".format(subTotalPrice)
        paymentBhimUpiBtn.text = "Pay \$%.2f".format(subTotalPrice)

        confirmPaymentBtn.setOnClickListener { orderDone() }
        paymentWalletBtn.setOnClickListener { orderDone() }
        paymentCreditDebitBtn.setOnClickListener {  }
        paymentBhimUpiBtn.setOnClickListener {  }
    }





    fun chooseModeOfPayment(view: View) {
        var payMethod = ""
        payMethod = if(view is RadioButton) {
            ((view.parent as LinearLayout).getChildAt(1) as TextView).text.toString()
        } else {
            (((view as CardView).getChildAt(0) as LinearLayout).getChildAt(1) as TextView).text.toString()
        }

        cashPaymentRB.isChecked = false
        walletsRB.isChecked = false
        savedCardRB.isChecked = false
        creditDebitRB.isChecked = false
        bhimUpiRB.isChecked = false
        netBankingRB.isChecked = false

        walletSection.visibility = ViewGroup.GONE
        creditDebitSection.visibility = ViewGroup.GONE
        bhimUpiSection.visibility = ViewGroup.GONE
        savedCardRecyclerView.visibility = ViewGroup.GONE

        confirmPaymentBtn.visibility = ViewGroup.INVISIBLE

        when(payMethod) {
            getString(R.string.cash_payment) -> {
                cashPaymentRB.isChecked = true
                confirmPaymentBtn.visibility = ViewGroup.VISIBLE
            }
            getString(R.string.wallets) -> {
                walletsRB.isChecked = true
                walletSection.visibility = ViewGroup.VISIBLE
            }
            getString(R.string.saved_cards) -> {
                savedCardRB.isChecked = true
                savedCardRecyclerView.visibility = ViewGroup.VISIBLE
                loadSavedCardsFromDatabase()
            }
            getString(R.string.credit_or_debit_card) -> {
                creditDebitRB.isChecked = true
                creditDebitSection.visibility = ViewGroup.VISIBLE
            }
            getString(R.string.bhim_upi) -> {
                bhimUpiRB.isChecked = true
                bhimUpiSection.visibility = ViewGroup.VISIBLE
            }
            getString(R.string.net_banking) -> {
                Toast.makeText(this, "NOT AVAILABLE", Toast.LENGTH_SHORT).show()
                netBankingRB.isChecked = true
            }
        }
    }

    private fun orderDone() {
        var paymentMethod = ""
        when {
            cashPaymentRB.isChecked -> paymentMethod = "Pending: Cash Payment"
            walletsRB.isChecked -> paymentMethod = "Paid: $selectedWallet Wallet"
            savedCardRB.isChecked -> paymentMethod = "Paid: $selectedSavedCard"
            creditDebitRB.isChecked -> paymentMethod = "Paid: $enteredCreditDebitCard"
            bhimUpiRB.isChecked -> paymentMethod = "Paid: $enteredUPI"
            //netBankingRB.isChecked -> paymentMethod = "Paid: Net Banking"
        }

        val intent = Intent(this, OrderDoneActivity::class.java)
        intent.putExtra("totalItemPrice", totalItemPrice)
        intent.putExtra("totalTaxPrice", totalTaxPrice)
        intent.putExtra("subTotalPrice", subTotalPrice)
        intent.putExtra("takeAwayTime", takeAwayTime)
        intent.putExtra("paymentMethod", paymentMethod)

        startActivity(intent)
        finish()
    }

    private fun loadSavedCardsFromDatabase() {
        //savedCardItems.clear()
        //savedCardsRecyclerAdapter.notifyDataSetChanged()

        //val data = DatabaseHandler(this).readSavedCardsData()
//
        //if(data.size == 0) {
        //    Toast.makeText(this, "No Saved Cards", Toast.LENGTH_SHORT).show()
        //    return
        //}
//
        //for(i in 0 until data.size) {
        //    val currentItem =  SavedCardItem()
        //    currentItem.cardNumber = data[i].cardNumber
        //    currentItem.cardHolderName = data[i].cardHolderName
        //    currentItem.cardExpiryDate = data[i].cardExpiryDate
//
        //    savedCardItems.add(currentItem)
        //    savedCardsRecyclerAdapter.notifyItemInserted(i)
        //}

    }

}