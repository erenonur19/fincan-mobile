package interfaces

import datamodels.MenuItem

interface ItemApi {
    fun onFetchSuccessListener(list:ArrayList<MenuItem>)
}