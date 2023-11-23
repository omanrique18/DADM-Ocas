package co.edu.unal.reto8

import android.app.Application
import androidx.room.Room

class EnterpriseApplication: Application() {
    companion object{
        lateinit var dataBase: EnterpriseDB
    }

    override fun onCreate() {
        super.onCreate()
        dataBase = Room.databaseBuilder(
            this,
            EnterpriseDB::class.java,
            "EnterpriseDB").build()
    }
}