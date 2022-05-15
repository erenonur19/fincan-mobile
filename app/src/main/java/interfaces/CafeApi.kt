package interfaces;


import datamodels.CafeItem
import datamodels.MenuItem

interface CafeApi {
    fun onFetchSuccessListener(list:ArrayList<CafeItem>)
}