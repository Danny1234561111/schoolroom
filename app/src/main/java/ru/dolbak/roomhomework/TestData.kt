package ru.dolbak.roomhomework
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TestData {
    fun populateDatabase(schoolDao: SchoolDao) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            // Students
            val student1 = Student(name = "Alice", grade = 10)
            val student2 = Student(name = "Bob", grade = 11)
            val student3 = Student(name = "Charlie", grade = 10)
            val student4 = Student(name = "David", grade = 12)

            val student1Id = schoolDao.insertStudent(student1) // Получаем сгенерированный ID
            val student2Id = schoolDao.insertStudent(student2)
            val student3Id = schoolDao.insertStudent(student3)
            val student4Id = schoolDao.insertStudent(student4)

            // Subjects
            val subject1 = Subject(name = "Math", teacher = "Mr. Smith")
            val subject2 = Subject(name = "Science", teacher = "Mrs. Jones")
            val subject3 = Subject(name = "History", teacher = "Mr. Brown")

            val subject1Id = schoolDao.insertSubject(subject1) // Получаем сгенерированный ID
            val subject2Id = schoolDao.insertSubject(subject2)
            val subject3Id = schoolDao.insertSubject(subject3)

            // StudentSubjectCrossRef
            schoolDao.insertStudentSubjectCrossRef(StudentSubjectCrossRef(student1Id.toInt(), subject1Id.toInt())) // Используем полученные ID
            schoolDao.insertStudentSubjectCrossRef(StudentSubjectCrossRef(student1Id.toInt(), subject2Id.toInt()))
            schoolDao.insertStudentSubjectCrossRef(StudentSubjectCrossRef(student2Id.toInt(), subject2Id.toInt()))
            schoolDao.insertStudentSubjectCrossRef(StudentSubjectCrossRef(student2Id.toInt(), subject3Id.toInt()))
            schoolDao.insertStudentSubjectCrossRef(StudentSubjectCrossRef(student3Id.toInt(), subject1Id.toInt()))
            schoolDao.insertStudentSubjectCrossRef(StudentSubjectCrossRef(student4Id.toInt(), subject3Id.toInt()))
        }
    }
}