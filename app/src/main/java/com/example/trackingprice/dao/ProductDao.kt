package com.example.trackingprice.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.trackingprice.model.Product

@Dao
interface ProductDao{

    @Query("SELECT * FROM product_table")
    fun getAllProductLiveData():LiveData<List<Product>>

    @Query("SELECT * FROM product_table")
    fun getAllProduct():List<Product>


    @Query("SELECT * FROM product_table WHERE product_url =:url")
    fun getProductByUrl(url:String):Product

    @Update()
    fun updateProduct(product:Product)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(product:Product)

    @Query("DELETE FROM product_table")
    fun delete()

}