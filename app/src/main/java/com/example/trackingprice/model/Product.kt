package com.example.trackingprice.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "product_table")
data class Product(
    @PrimaryKey()
    @ColumnInfo(name = "product_url")
    val product_url:String,

    @ColumnInfo(name = "product_name")
    val product_name:String,

    @ColumnInfo(name = "product_price")
    var product_price:Int,

    @ColumnInfo(name = "product_price_history")
    var product_price_history:ArrayList<Int>,

    @ColumnInfo(name = "product_image_dir")
    var product_image_dir:String,

    @ColumnInfo(name = "product_last_checked")
    var product_last_checked:Long,

    @ColumnInfo(name = "product_price_trend")
    var product_price_trend:Int,

    @ColumnInfo(name = "product_added_date")
    val product_added_date:Long





)
