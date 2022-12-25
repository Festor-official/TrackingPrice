package com.example.trackingprice.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.util.Currency
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.trackingprice.database.ProductDatabase
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.*

import org.jsoup.Jsoup
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
class CheckPriceWorker(ctx: Context, params:WorkerParameters): CoroutineWorker(ctx, params) {

    val mContext = ctx
    val checkPriceCoroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun doWork(): Result {

        return try {
            val productUrlList = inputData.getStringArray("productUrlList")
            checkPriceCoroutineScope.launch {
                if (productUrlList != null) {
                    Log.v("MainActivity",productUrlList.size.toString())
                }
                productUrlList?.forEach { link ->

                    checkPriceOzon(link)
                }
                cancel()
            }

            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }


    private fun checkPriceOzon(link:String){
        try {

            val doc = Jsoup.connect(link)
                .userAgent("Mozilla").get()

            val divList = doc.select("div")
            var currentProductPrice = ""
            for (div in divList){
                if("при оплате Ozon Картой" in div.text() && div.hasClass("_3-a5")){
                    currentProductPrice = div.text().removeSuffix("₽ при оплате Ozon Картой")
                    currentProductPrice = currentProductPrice.replace(" ","")
                    }
            }
            val productDao = ProductDatabase.getDatabase(mContext.applicationContext).productDao()
            val product = productDao.getProductByUrl(link)

            if(currentProductPrice != ""){

                product.product_last_checked = Calendar.getInstance().timeInMillis

                if(currentProductPrice.toInt() != product.product_price){

                    product.product_price_trend =  currentProductPrice.toInt()- product.product_price
                    product.product_price = currentProductPrice.toInt()
                    product.product_price_history.add(currentProductPrice.toInt())

                    if(product.product_price_trend<0){
                        priceDropNotification(doc.title(),product.product_price_trend,currentProductPrice.toInt())
                    }
                }
                productDao.updateProduct(product)

            }




        } catch (e: IOException) {

        }
    }

    private fun priceDropNotification(productName:String,discount:Int,currentPrice:Int){
        val message = "Discount for the ${productName.subSequence(0,15)}...,\n minus $discount₽ ,current price $currentPrice₽"
        val context = applicationContext
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel  = NotificationChannel(CHANNEL_ID,name,importance)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)

        Log.v("MainActivity","notify")

        val builder = NotificationCompat.Builder(context,CHANNEL_ID)
            .setContentTitle("PriceTracking")
            .setSmallIcon(android.R.drawable.btn_dialog)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())

    }

//    private fun checkPriceWildberries(link: String){
//        try {
//
//            val doc = Jsoup.connect(link)
//                .userAgent("Mozilla").get()
//
//            var price = doc.select("div")
//
//            for (i in price){
//                if("₽" in i.text()){
//                    var mon = i.text()
//                    mon = mon.replace(" ","")
//                }
//
//            }
//        } catch (e: IOException) {
//            Log.v("MainActivity","err")
//        }
//    }



}