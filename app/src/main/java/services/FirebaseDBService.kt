package services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import datamodels.CafeItem
import datamodels.MenuItem
import datamodels.OrderItem
import interfaces.CafeApi
import interfaces.ItemApi
import interfaces.OrderApi
import java.util.*
import kotlin.collections.ArrayList

class FirebaseDBService {
    private var databaseRef: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun readAllCafe(menuApi: CafeApi) {
        val menuList = ArrayList<CafeItem>()

        databaseRef.collection("caffee").get().addOnSuccessListener {
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

    fun readAllOrder(orderApi: OrderApi){
        val orderList1 = ArrayList<OrderItem>()
        val orderList2 = ArrayList<OrderItem>()
        databaseRef.collection("orders").get().addOnSuccessListener {
            for(a in it.documents){
                if (a["email"]?.equals(FirebaseAuth.getInstance().currentUser!!.email.toString()) == true){
                    var order = OrderItem(
                        status = a["watch"] as String,
                        subtotalPrice = (a["subtotalPrice"] as Double).toFloat(),
                        paymentMethod = a["paymentMethod"] as String,
                        orderId = a["orderId"] as String,
                        items = a["items"] as ArrayList<Map<Any,Any>>,
                        time = a["time"] as String
                    )
                    orderList1.add(order)
                }
            }
                databaseRef.collection("caffee").get().addOnSuccessListener {
                    for (order in orderList1){
                        for(a in it.documents){
                            if(a["restaurantkey"] as String == order.items[0]["cafeKey"] as String){
                                var newOrder = OrderItem(
                                    status = order.status,
                                    subtotalPrice = order.subtotalPrice,
                                    paymentMethod = order.paymentMethod,
                                    orderId = order.orderId,
                                    items = order.items,
                                    time = order.time,
                                    cafeName = a["name"] as String,
                                    cafeImageUrl = a["imageurl"] as String,
                                )

                                orderList2.add(newOrder)
                            }
                        }
                }
                orderList2.sort()

                orderApi.onFetchSuccessListener(orderList2)
            }
        }
    }


    fun pushOrder(basket: MutableList<MenuItem>, orderId: String, paymentMethod: String, subtotalPrice: Float, time: String){
        val auth = FirebaseAuth.getInstance().currentUser!!
        var myMap = mapOf<String, Any>(
            "orderId" to orderId,
            "paymentMethod" to paymentMethod,
            "subtotalPrice" to subtotalPrice,
            "time" to time,
            "restaurantId" to basket[0].cafeKey,
            "watch" to "Pending",
            "items" to basket,
            "email" to auth.email.toString()
        )

        databaseRef.collection("orders").add(myMap)
    }



}
