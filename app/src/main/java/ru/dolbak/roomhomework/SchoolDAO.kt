package ru.dolbak.roomhomework
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long  // Изменено на Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long  // Изменено на Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentSubjectCrossRef(crossRef: StudentSubjectCrossRef)

    @Query("SELECT s.* FROM students s INNER JOIN StudentSubjectCrossRef cr ON s.id = cr.studentId WHERE cr.subjectId = :subjectId")
    fun getStudentsForSubject(subjectId: Int): Flow<List<Student>>

    @Query("SELECT sub.* FROM subjects sub INNER JOIN StudentSubjectCrossRef cr ON sub.id = cr.subjectId WHERE cr.studentId = :studentId")
    fun getSubjectsForStudent(studentId: Int): Flow<List<Subject>>

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>
}