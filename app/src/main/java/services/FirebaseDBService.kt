package services

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
            menuList.shuffle() //so that every time user can see different items on opening app
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
            itemList.shuffle() //so that every time user can see different items on opening app
            itemApi.onFetchSuccessListener(itemList)
        }
    }

}
