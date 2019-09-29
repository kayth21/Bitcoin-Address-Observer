package com.ceaver.bao.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ceaver.bao.Application
import com.ceaver.bao.addresses.Address
import com.ceaver.bao.addresses.AddressDao
import com.ceaver.bao.logging.Log
import com.ceaver.bao.logging.LogDao

@androidx.room.Database(entities = [Address::class, Log::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun addressDao(): AddressDao
    abstract fun logDao(): LogDao

    companion object {
        private var INSTANCE: Database? = null

        fun getInstance(): Database {
            if (INSTANCE == null) {
                synchronized(Database::class) {
                    INSTANCE = Room.databaseBuilder(Application.appContext!!, Database::class.java, "database").build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}