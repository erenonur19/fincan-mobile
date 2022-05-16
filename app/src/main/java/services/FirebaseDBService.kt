package services

import android.view.Menu
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import datamodels.CafeItem
import datamodels.MenuItem
import interfaces.CafeApi
import interfaces.ItemApi
import java.util.*
import kotlin.collections.ArrayList

class FirebaseDBService {
    private var databaseRef: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun readAllCafe(menuApi: CafeApi) {
        val menuList = ArrayList<CafeItem>()

        databaseRef.collection("resturant").get().addOnSuccessListener {
            for(a in it.documents){
                val item = CafeItem(
                    email = a["email"].toString(),
                    cafeKey = a["restaurantkey"].toString(),
                    imageUrl = a["imageurl"].toString(),
                    cafeName = a["name"].toString(),
                    cafeAddress = a["address"].toString(),
                    cafeStars = 5f,
                )
                menuList.add(item)
            }

            menuApi.onFetchSuccessListener(menuList)
        }
    }

    fun readAllItem(itemApi: ItemApi) {
        val itemList = ArrayList<MenuItem>()

        databaseRef.collection("items").get().addOnSuccessListener {
            for(a in it.documents){
                val item = MenuItem(
                    cafeKey = a["key"].toString(),
                    imageUrl = a["imageurl"].toString(),
                    itemName= a["itemname"].toString(),
                    itemCategory = a["itemcategory"].toString(),
                    itemStars = 5f,
                    itemPrice = a["itemprice"].toString().toFloat(),
                )
                itemList.add(item)
            }
            itemApi.onFetchSuccessListener(itemList)
        }
    }

    fun pushOrder(basket: MutableList<MenuItem>, orderId: String, paymentMethod: String, subtotalPrice: Float, time: String){

        var myMap = mapOf<String, Any>(
            "orderId" to orderId,
            "paymentMethod" to paymentMethod,
            "subtotalPrice" to subtotalPrice,
            "time" to time,
            "restaurantId" to basket[0].cafeKey,
            "watch" to "Pending",
            "items" to basket
        )

        databaseRef.collection("orders").add(myMap)
    }

}
