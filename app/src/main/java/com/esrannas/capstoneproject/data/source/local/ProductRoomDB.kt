package com.esrannas.capstoneproject.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.esrannas.capstoneproject.data.model.response.ProductEntity

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class ProductRoomDB : RoomDatabase() {
    abstract fun productDao(): ProductDao
}