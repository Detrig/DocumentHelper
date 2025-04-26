package com.example.documenthelper.core

sealed class Result {
    data object Success : Result()
    data class Error(val message: String) : Result()
}