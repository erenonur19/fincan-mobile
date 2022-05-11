package datamodels

data class MenuItem(
    var imageUrl: String = "IMAGE_URL",
    var cafeKey: String = "key",
    var itemPrice: Float = 10.5f,
    var itemName: String = "ITEM_NAME",
    var itemCategory: String = "CATEGORY_NAME",
    var itemStars: Float = 0f,
)
