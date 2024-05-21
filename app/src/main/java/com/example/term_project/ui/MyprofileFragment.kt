package com.example.term_project.ui

import MainViewModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.databinding.FragmentMyprofileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class MyprofileFragment : Fragment(){

    private lateinit var binding : FragmentMyprofileBinding
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyprofileBinding.inflate(layoutInflater)

        val spf = requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        val uid = spf.getString("uid", "")

        binding.myprofileAnalysisCv.setOnClickListener{
            val intent = Intent(requireContext(), NoteActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.myprofileRecordCv.setOnClickListener{
            val intent = Intent(requireContext(), AnalyzeActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.myprofileQuestionLayoutLl.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/9Xh76RBmzmS2BvRT9"))
            requireActivity().startActivity(intent)
        }

        getUser(uid!!)

        return binding.root
    }

    private fun getUser(uid: String) {
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
                binding.myprofileNameTv.text = diaryList[0].name
                binding.myprofileEmailTv.text =  diaryList[0].email
                binding.myprofileInfoTv.text = diaryList[0].info
            }
            .addOnFailureListener { exception ->
                println("쿼리 실패: $exception")
            }
    }
}