package com.example.trackingprice.database

import android.content.Context
import androidx.room.*
import com.example.trackingprice.converter.Converter
import com.example.trackingprice.dao.ProductDao
import com.example.trackingprice.model.Product

@Database(entities = [Product::class], version = 2)
@TypeConverters(Converter::class)
abstract class ProductDatabase():RoomDatabase() {

    abstract fun productDao():ProductDao

    companion object{
        @Volatile
        private var INSTANCEPRODUCT:ProductDatabase? = null

        fun getDatabase(context: Context):ProductDatabase{
            return INSTANCEPRODUCT ?: synchronized(this){
                val instanceProduct = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCEPRODUCT = instanceProduct
                return  instanceProduct
            }
        }



    }




}