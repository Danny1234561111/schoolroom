package ru.dolbak.roomhomework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.*

class StatActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "results.db"
        ).build()

        val totalCapitalizationTextView = findViewById<TextView>(R.id.money)
        val companiesAboveAverageTextView = findViewById<TextView>(R.id.good)
        val englishCompaniesTextView = findViewById<TextView>(R.id.english)
        val highestCapitalizationTextView = findViewById<TextView>(R.id.best)
        val longestNameTextView = findViewById<TextView>(R.id.longest)

        db.resultsDao().getAll("RESULT DESC").observe(this) { results ->
            CoroutineScope(Dispatchers.Default).launch {
                // Общая капитализация
                val totalCapitalization = results.sumOf { it.result ?: 0 }
                withContext(Dispatchers.Main) {
                    totalCapitalizationTextView.text = totalCapitalization.toString()
                }

                // Компании с капитализацией выше среднего
                val averageCapitalization = if (results.isNotEmpty()) totalCapitalization / results.size else 0
                val companiesAboveAverage = results.count { (it.result ?: 0) > averageCapitalization }
                withContext(Dispatchers.Main) {
                    companiesAboveAverageTextView.text = companiesAboveAverage.toString()
                }

                // Компании с англоязычными названиями
                val englishCompaniesCount = results.count { it.name?.any { char -> char.isLetter() && char.isUpperCase() } == true }
                withContext(Dispatchers.Main) {
                    englishCompaniesTextView.text = englishCompaniesCount.toString()
                }

                // Компания с самой высокой капитализацией
                val highestCapitalizationCompany = results.maxByOrNull { it.result ?: 0 }
                withContext(Dispatchers.Main) {
                    highestCapitalizationTextView.text = highestCapitalizationCompany?.name ?: "Нет данных"
                }

                // Компания с самым длинным названием
                val longestNameCompany = results.maxByOrNull { it.name?.length ?: 0 }
                withContext(Dispatchers.Main) {
                    longestNameTextView.text = longestNameCompany?.name ?: "Нет данных"
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        db.close() // Закрытие базы данных
    }
}
