import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.Note
import com.example.term_project.data.entity.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val _documents = MutableLiveData<List<Diary>>()
    val _note = MutableLiveData<Note>()
    val _noteList = MutableLiveData<List<Note>>()
    val _user = MutableLiveData<UserInfo>()

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


    fun getInitAllNote(uid : String) {
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
                _note.value = _noteList.value!![0]
                Log.d("noteList", "${_noteList.value}")
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

    fun getUser(uid: String) {
        db.collection("clients")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                val diaryList = mutableListOf<UserInfo>()
                for (document in documents) {
                    val diary = document.toObject(UserInfo::class.java)
                    diaryList.add(diary)
                }
                Log.d("MainViewModel", diaryList.toString())
                _user.value = diaryList[0]
            }
            .addOnFailureListener { exception ->
                println("쿼리 실패: $exception")
            }
    }
}