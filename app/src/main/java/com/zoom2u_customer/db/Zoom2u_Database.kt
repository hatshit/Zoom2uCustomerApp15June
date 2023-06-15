package com.zoom2u_customer.db

import android.content.Context
import androidx.room.Room

import androidx.room.RoomDatabase
import com.zoom2u_customer.utility.Zoom2uContractProvider


//@Database(entities = [], version = 1,exportSchema = false)
abstract class Zoom2u_Database : RoomDatabase(){


    companion object {

        /** The only instance  */
        private var sInstance: Zoom2u_Database? = null

        @Synchronized
        fun getInstance(context: Context): Zoom2u_Database? {
            if (sInstance == null) {
                synchronized(Zoom2u_Database::class) {

                        sInstance = Room
                            .databaseBuilder(
                                context.applicationContext,
                                Zoom2u_Database::class.java,
                                Zoom2uContractProvider.DB_NAME
                            ).fallbackToDestructiveMigration()
                            .build()

                }
            }
            return sInstance
        }
    }

}