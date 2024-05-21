import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.Note
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val _documents = MutableLiveData<List<Diary>>() //일기 저장된 부분
    val _note = MutableLiveData<Int>()
    val _noteList = MutableLiveData<List<Note>>()

    init {
        _note.value = 1
    }

    fun getAllDiary(uid: String) {
        db.collection("diary")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                val diaryList = mutableListOf<Diary>()
                for (document in documents) {
                    val diary = document.toObject(Diary::class.java)
                    diaryList.add(diary)
                }
                _documents.value = diaryList
                Log.d("MainViewModel", "${_documents.value}")
            }
            .addOnFailureListener { exception ->
                println("쿼리 실패: $exception")
            }
    }

    fun getAllNote(uid : String) {
        db.collection("note")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                val noteList = mutableListOf<Note>()
                for (document in documents) {
                    val note = document.toObject(Note::class.java)
                    noteList.add(note)
                }
                _noteList.value = noteList
                Log.d("noteList", "${_noteList.value}")
            }
            .addOnFailureListener { exception ->
                println("쿼리 실패: $exception")
            }
    }
}