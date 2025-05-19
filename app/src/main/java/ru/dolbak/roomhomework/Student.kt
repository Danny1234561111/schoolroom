package ru.dolbak.roomhomework

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val grade: Int
) {
    override fun toString(): String {
        return name  // Для отображения в Spinner
    }
}