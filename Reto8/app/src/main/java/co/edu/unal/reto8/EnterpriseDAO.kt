package co.edu.unal.reto8

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EnterpriseDAO {
    @Query("SELECT * FROM EnterpriseEntity")
    fun getAllEnterprises(): MutableList<EnterpriseEntity>

    @Query("SELECT * FROM EnterpriseEntity where id = :id")
    fun getEnterpriseById(id: Long): EnterpriseEntity

    @Insert
    fun addEnterprise(enterpriseEntity: EnterpriseEntity): Long

    @Update
    fun updateEnterprise(enterpriseEntity: EnterpriseEntity)

    @Delete
    fun deleteEnterprise(enterpriseEntity: EnterpriseEntity)
}