package com.example.mkart.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull


@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey
    @NonNull
    val productId:String,
    @ColumnInfo("productName")
    val productName:String?="",
    @ColumnInfo("productImage")
    val productImage:String?="",
    @ColumnInfo("productSp")
    val productSp:String?=""
)
