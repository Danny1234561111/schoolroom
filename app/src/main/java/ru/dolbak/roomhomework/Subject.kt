package ru.dolbak.roomhomework

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val teacher: String
){
    override fun toString(): String {
        return name  // Для отображения в Spinner
    }
}