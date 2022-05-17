package datamodels;


data class OrderItem(
    var cafeName: String = "CAFE_NAME",
    var cafeImageUrl: String = "IMAGE_URL",
    var status: String = "STATUS",
    var subtotalPrice: Float = 0f,
    var paymentMethod: String = "PAYMENT_METHOD",
    var time: String  = "TIME",
    var orderId: String = "ORDER_ID",
    var items: ArrayList<Map<Any, Any>> = arrayListOf<Map<Any,Any>>()
)