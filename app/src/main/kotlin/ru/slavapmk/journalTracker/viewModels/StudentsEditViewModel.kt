package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.studentsEdit.StudentsEditListItem
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity

class StudentsEditViewModel : ViewModel() {
    fun loadStudents() {
        viewModelScope.launch {
            val students = StorageDependencies.studentRepository.getStudents().map {
                StudentsEditListItem(
                    it.id,
                    it.name
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
                    null
                )
            )
            updateStudentLiveData.postValue(
                Pair(
                    insert, StudentsEditListItem(
                        id,
                        insert.name
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
}