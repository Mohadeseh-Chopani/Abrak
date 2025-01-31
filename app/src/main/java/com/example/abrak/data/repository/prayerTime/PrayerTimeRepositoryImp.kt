package com.example.abrak.data.repository.prayerTime

import com.example.abrak.data.dataSource.remote.prayerTime.PrayerTimeRemoteDataSourceImp
import com.example.abrak.data.models.PrayerTimeData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PrayerTimeRepositoryImp(val prayerTimeRemoteDataSourceImp: PrayerTimeRemoteDataSourceImp): PrayerTimeRepository {
    private var prayerTimeCompositDisposable: CompositeDisposable? = null

    override fun getPrayerTime(token: String, city: String): Single<PrayerTimeData> {
        return prayerTimeRemoteDataSourceImp.getPrayerTime(token, city)
            .doOnSubscribe { disposable -> prayerTimeCompositDisposable?.add(disposable) }
            .doOnSuccess {  }
            .doFinally {  }
            .doOnError {  }
    }

    fun clearDisposable() {
        prayerTimeCompositDisposable?.let { it.clear() }
    }
}