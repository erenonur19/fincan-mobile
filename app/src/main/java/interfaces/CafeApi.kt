package interfaces;

import datamodels.CafeItem;

enum class RequestType {
    READ, OFFLINE_UPDATE
}

interface CafeApi {
    fun onFetchSuccessListener(list:ArrayList<CafeItem>, requestType: RequestType)
}