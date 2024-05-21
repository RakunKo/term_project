package com.example.term_project.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.term_project.databinding.FragmentMyprofileBinding

class MyprofileFragment : Fragment(){

    private lateinit var binding : FragmentMyprofileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyprofileBinding.inflate(layoutInflater)

        binding.myprofileAnalysisCv.setOnClickListener{
            val intent = Intent(requireContext(), NoteActivity::class.java)
            requireActivity().startActivity(intent)
        }
        return binding.root
    }
}