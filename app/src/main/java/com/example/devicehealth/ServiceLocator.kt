package com.example.devicehealth

import android.content.Context
import androidx.room.Room
import com.example.devicehealth.data.local.UserDatabase
import com.example.devicehealth.repository.TipApiService
import com.example.devicehealth.data.repository.TipRepository
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    // call ServiceLocator.init(context) from Application or AppRoot on startup
    private var db: UserDatabase? = null
    private var retrofitBaseCreated = false

    fun init(context: Context) {
        if (db == null) {
            db = Room.databaseBuilder(context.applicationContext, UserDatabase::class.java, "device_db")
                .fallbackToDestructiveMigration() // dev: replace with proper migrations for prod
                .build()
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.techy.dev/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    val tipApi: TipApiService by lazy { retrofit.create(TipApiService::class.java) }
    val tipRepository: TipRepository
        get() = TipRepository(tipApi, db!!.tipDao())
}
