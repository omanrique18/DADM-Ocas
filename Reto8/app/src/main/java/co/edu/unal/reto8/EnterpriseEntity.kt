package co.edu.unal.reto8

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EnterpriseEntity")
data class EnterpriseEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var url: String,
    var phone: String,
    var email: String,
    var productsAndServices: String,
    var classification: String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnterpriseEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}