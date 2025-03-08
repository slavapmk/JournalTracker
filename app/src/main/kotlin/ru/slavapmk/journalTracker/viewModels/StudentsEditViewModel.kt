package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.studentsEdit.StudentsEditListItem
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.ui.lesson.toEdit
import ru.slavapmk.journalTracker.ui.lesson.toEntity

class StudentsEditViewModel : ViewModel() {
    fun loadStudents() {
        viewModelScope.launch {
            val students = StorageDependencies.studentRepository.getStudents().map {
                StudentsEditListItem(
                    it.id,
                    it.name,
                    it.default.toEdit()
                )
            }
            studentsLiveData.postValue(students)
        }
    }

    var studentName = ""
    val studentsList: MutableList<StudentsEditListItem> = mutableListOf()

    val studentsLiveData: MutableLiveData<List<StudentsEditListItem>> by lazy {
        MutableLiveData()
    }

    val updateStudentLiveData: MutableLiveData<Pair<StudentsEditListItem, StudentsEditListItem>> by lazy {
        MutableLiveData()
    }

    fun addStudent(insert: StudentsEditListItem): Int {
        studentsList.add(insert)
        viewModelScope.launch {
            val id = StorageDependencies.studentRepository.insertStudent(
                StudentEntity(
                    0,
                    insert.name,
                    insert.default.toEntity()
                )
            )
            updateStudentLiveData.postValue(
                Pair(
                    insert, StudentsEditListItem(
                        id,
                        insert.name,
                        insert.default
                    )
                )
            )
        }
        studentsList.sortBy { it.name }
        return studentsList.indexOf(insert)
    }

    fun deleteStudent(student: StudentsEditListItem) {
        viewModelScope.launch {
            StorageDependencies.studentRepository.deleteStudent(student.id!!)
        }
    }

    fun updateStudent(student: StudentsEditListItem) {
        viewModelScope.launch {
            StorageDependencies.studentRepository.updateStudent(
                StudentEntity(
                    student.id ?: 0,
                    student.name,
                    student.default.toEntity()
                )
            )
        }
    }
}