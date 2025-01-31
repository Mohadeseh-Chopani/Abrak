package com.example.abrak.ui.viewModel.prayerTime

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abrak.data.models.PrayerTimeData
import com.example.abrak.data.repository.prayerTime.PrayerTimeRepositoryImp
import com.example.abrak.network.api.prayerTimeAPI.PrayerTimeApiServiceProvider
import com.example.abrak.utils.NetworkState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class PrayerTimeViewModel(private val prayerTimeRepositoryImp: PrayerTimeRepositoryImp) : ViewModel() {
    private val _CurrentTime = MutableLiveData<NetworkState<PrayerTimeData>>()
    val CurrentTime: LiveData<NetworkState<PrayerTimeData>> get() = _CurrentTime

    @SuppressLint("CheckResult")
    fun getPrayerTime(city: String) {
        _CurrentTime.value = NetworkState.Loading
        prayerTimeRepositoryImp.getPrayerTime(PrayerTimeApiServiceProvider.API_KEY, city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                _CurrentTime.value = NetworkState.Success(response)
                Log.d("XMOH", "PrayerTimeData: success" + response)
            }, { throwable ->
                _CurrentTime.value = NetworkState.Error(throwable)
                Log.d("XMOH", "PrayerTimeData: error" + throwable.message)
            })
    }

    override fun onCleared() {
        super.onCleared()
        prayerTimeRepositoryImp.clearDisposable()
    }
}