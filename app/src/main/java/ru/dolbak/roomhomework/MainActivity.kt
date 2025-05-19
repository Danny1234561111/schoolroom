package ru.dolbak.roomhomework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "results.db"
        ).build()

        // Заполнение базы данных тестовыми данными
        CoroutineScope(Dispatchers.IO).launch {
            db.resultsDao().insert(*TestData.russianCompanies2020.toTypedArray())
        }

        val companiesList = findViewById<RecyclerView>(R.id.companies_list)
        val statisticsButton = findViewById<Button>(R.id.statistics)
        val deleteButton = findViewById<Button>(R.id.delete)
        val deleteText = findViewById<TextView>(R.id.toDelete)

        companiesList.layoutManager = LinearLayoutManager(this)

        // Получение всех результатов и установка адаптера
        db.resultsDao().getAll("RESULT DESC").observe(this) { results ->
            companiesList.adapter = ResultAdapter(results)
        }

        statisticsButton.setOnClickListener {
            startActivity(Intent(this, StatActivity::class.java))
        }

        deleteButton.setOnClickListener {
            val nameToDelete = deleteText.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                // Удаление результата по имени
                db.resultsDao().deleteByNameContaining(nameToDelete)
                // После удаления обновляем список
                val updatedResults = db.resultsDao().getAll("RESULT DESC").value ?: emptyList()
                withContext(Dispatchers.Main) {
                    companiesList.adapter = ResultAdapter(updatedResults)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close() // Закрытие базы данных
    }
}
