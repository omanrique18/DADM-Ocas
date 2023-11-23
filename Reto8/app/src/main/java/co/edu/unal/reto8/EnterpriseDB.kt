package co.edu.unal.reto8

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EnterpriseEntity::class], version = 1)
abstract class EnterpriseDB: RoomDatabase() {
    abstract fun enterpriseDAO(): EnterpriseDAO
}