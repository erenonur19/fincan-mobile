package interfaces

import datamodels.OrderItem

interface OrderApi {
    fun onFetchSuccessListener(list:ArrayList<OrderItem>)
}