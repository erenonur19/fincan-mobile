package datamodels;

import com.google.firestore.v1.StructuredQuery


data class OrderItem(
    var cafeName: String = "CAFE_NAME",
    var cafeImageUrl: String = "IMAGE_URL",
    var status: String = "STATUS",
    var subtotalPrice: Float = 0f,
    var paymentMethod: String = "PAYMENT_METHOD",
    var time: String  = "TIME",
    var orderId: String = "ORDER_ID",
    var items: ArrayList<Map<Any, Any>> = arrayListOf<Map<Any,Any>>()
): Comparable<OrderItem>{
    override fun compareTo(other: OrderItem): Int {
        if (this.time < other.time){
            return -1;
        }else{
            return 1;
        }
    }
}