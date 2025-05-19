
package ru.dolbak.roomhomework

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.widget.Toast
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioStudent: RadioButton
    private lateinit var radioSubject: RadioButton
    private lateinit var spinner: Spinner
    private lateinit var resultTextView: TextView

    private lateinit var schoolDao: SchoolDao
    private lateinit var database: SchoolDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        radioGroup = findViewById(R.id.radioGroup)
        radioStudent = findViewById(R.id.radioStudent)
        radioSubject = findViewById(R.id.radioSubject)
        spinner = findViewById(R.id.spinner)
        resultTextView = findViewById(R.id.resultTextView)

        database = SchoolDatabase.getInstance(applicationContext)
        schoolDao = database.schoolDao

        // Заполняем базу данных тестовыми данными (один раз при первом запуске)
        val sharedPref = getPreferences(MODE_PRIVATE)
        val isDatabasePopulated = sharedPref.getBoolean("database_populated", false)
        if (!isDatabasePopulated) {
            CoroutineScope(Dispatchers.IO).launch { // Запускаем TestData в IO потоке
                TestData.populateDatabase(schoolDao)
                withContext(Dispatchers.Main) {
                    with(sharedPref.edit()) {
                        putBoolean("database_populated", true)
                        apply()
                    }
                }
            }
        }

        // Инициализация Spinner
        setupSpinner()

        // Обработчик RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            setupSpinner() // Перезагружаем Spinner при смене RadioButton
        }

        // Обработчик выбора элемента в Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                displayResults()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                resultTextView.text = ""
            }
        }
    }

    private fun setupSpinner() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            if (radioStudent.isChecked) {
                schoolDao.getAllStudents().collectLatest { students ->
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, students)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            } else {
                schoolDao.getAllSubjects().collectLatest { subjects ->
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, subjects)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            }
        }
    }

    private fun displayResults() {
        val selectedItem = spinner.selectedItem

        if (selectedItem == null) {
            resultTextView.text = ""
            return
        }

        CoroutineScope(Dispatchers.IO).launch { // IO Scope для всех запросов к БД
            try {
                if (radioStudent.isChecked) {
                    val student = selectedItem as Student
                    schoolDao.getSubjectsForStudent(student.id)
                        .catch { e ->
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, "Ошибка при загрузке предметов: ${e.message}", Toast.LENGTH_SHORT).show()
                                resultTextView.text = "Ошибка при получении данных о предметах."
                            }
                        }
                        .collectLatest { subjects ->
                            val text = if (subjects.isNotEmpty()) {
                                "Предметы: ${subjects.joinToString { it.name }}"
                            } else {
                                "Ученик не записан ни на один предмет."
                            }
                            withContext(Dispatchers.Main) {
                                resultTextView.text = text
                            }
                        }
                } else {
                    val subject = selectedItem as Subject
                    schoolDao.getStudentsForSubject(subject.id)
                        .catch { e ->
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, "Ошибка при загрузке учеников: ${e.message}", Toast.LENGTH_SHORT).show()
                                resultTextView.text = "Ошибка при получении данных об учениках."
                            }
                        }
                        .collectLatest { students ->
                            val text = if (students.isNotEmpty()) {
                                "Ученики: ${students.joinToString { it.name }}"
                            } else {
                                "На предмет не записан ни один ученик."
                            }
                            withContext(Dispatchers.Main) {
                                resultTextView.text = text
                            }
                        }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Неожиданная ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                    resultTextView.text = "Произошла неожиданная ошибка."
                }
            }
        }
    }
}
