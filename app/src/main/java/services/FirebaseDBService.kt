package services

import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import datamodels.CafeItem
import interfaces.CafeApi
import interfaces.RequestType

class FirebaseDBService {
    private var databaseRef: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun readAllMenu(menuApi: CafeApi, requestType: RequestType) {
        val menuList = ArrayList<CafeItem>()

        databaseRef.collection("resturant").get().addOnSuccessListener {
            for(a in it.documents){
                val item = CafeItem(
                    email = a["email"].toString(),
                    cafeKey = a["restaurantKey"].toString(),
                    imageUrl = a["item_image_url"].toString(),
                    cafeName = a["name"].toString(),
                    cafeAddress = a["address"].toString(),
                    cafeStars = 5f,
                )
                menuList.add(item)
            }
            menuList.shuffle() //so that every time user can see different items on opening app
            menuApi.onFetchSuccessListener(menuList, requestType)

        }
    }

}
