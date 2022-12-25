package com.example.trackingprice.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.trackingprice.workers.CheckPriceWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class PriceCheckViewModel @Inject constructor(application:Application):ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    var _workerOutPut:LiveData<WorkInfo>? = null
    lateinit var checkPriceWorker: PeriodicWorkRequest

    internal fun checkPrice(links:ArrayList<String>){
        //workManager.enqueue(OneTimeWorkRequest.from(CheckPriceWorker::class.java))

        val data = Data.Builder().putStringArray("productUrlList",links.toTypedArray()).build()

        checkPriceWorker = PeriodicWorkRequestBuilder<CheckPriceWorker>(60,TimeUnit.MINUTES).setInputData(data).build()

        //workManager.enqueue(checkPriceWorker)
        workManager.enqueueUniquePeriodicWork("Check Price", ExistingPeriodicWorkPolicy.REPLACE, checkPriceWorker)

        //workerOutPut = workManager.getWorkInfosByTagLiveData("MY_KEY_DATA_FROM_WORKER") as MutableLiveData<List<WorkInfo>>

        _workerOutPut = workManager.getWorkInfoByIdLiveData(checkPriceWorker.id)


    }

    fun cancelWork(){
        workManager.cancelAllWork()
    }

    class PriceCheckViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PriceCheckViewModel::class.java)) {
                PriceCheckViewModel(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}