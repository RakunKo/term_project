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
    private lateinit var mainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyprofileBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.myprofileAnalysisCv.setOnClickListener{
            val intent = Intent(requireContext(), NoteActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.myprofileQuestionLayoutLl.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/9Xh76RBmzmS2BvRT9"))
            requireActivity().startActivity(intent)
        }
        binding.myprofileProfileEditCv.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            requireActivity().startActivity(intent)
        }
        if (mainViewModel._user.value != null) {
            binding.myprofileInfoTv.text = mainViewModel._user.value!!.info
            binding.myprofileNameTv.text = mainViewModel._user.value!!.name
            binding.myprofileEmailTv.text = mainViewModel._user.value!!.email
        }

        return binding.root
    }

}