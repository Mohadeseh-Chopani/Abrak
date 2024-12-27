package com.example.abrak.utils

sealed class NetworkState<out T> {
    object Loading : NetworkState<Nothing>()
    data class Success<T>(val data: T) : NetworkState<T>()
    data class Error(val throwable: Throwable) : NetworkState<Nothing>()
}