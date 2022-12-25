package com.example.trackingprice.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class Converter {

    @TypeConverter
    fun fromArrayIntToGson(priceHistory:ArrayList<Int>?): String? {
        val type = object: TypeToken<ArrayList<String>>() {}.type
        return Gson().toJson(priceHistory,type)
    }

    @TypeConverter
    fun fromGsonToArrayInt(todoString:String?):ArrayList<Int>{
        val type = object:TypeToken<ArrayList<Int>>() {}.type
        return Gson().fromJson<ArrayList<Int>>(todoString,type)
    }

}









