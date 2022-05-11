package datamodels

data class CafeItem(
    var email: String = "COFFEE_ID",
    var cafeName: String = "CAFE_NAME",
    var cafeAddress: String = "CAFE_ADDRESS",
    var imageUrl: String = "IMAGE_URL",
    var cafeKey: String = "key",
    var cafeStars: Float = 0f,
)