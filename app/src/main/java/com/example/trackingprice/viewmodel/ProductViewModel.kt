package com.example.trackingprice.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.trackingprice.dao.ProductDao
import com.example.trackingprice.database.ProductDatabase
import com.example.trackingprice.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(application:Application): ViewModel(){

    private val productDao: ProductDao
    private val databaseScope = CoroutineScope(Dispatchers.IO)


    init{
        productDao = ProductDatabase.getDatabase(application).productDao()
    }

    suspend fun getAllProductLiveData(): LiveData<List<Product>> {
            return productDao.getAllProductLiveData()

    }

    fun getAllProduct():List<Product>{
            return productDao.getAllProduct()

    }

    fun insert(product:Product){
        databaseScope.launch {

            productDao.insert(product)
        }

    }

    fun update(product: Product){
        databaseScope.launch {
            productDao.updateProduct(product)
        }
    }

    fun delete(){
        databaseScope.launch {
            productDao.delete()
        }
    }

}