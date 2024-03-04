package com.example.weatherex.dto

data class Body(
    val dataType: String,
    val items: Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)