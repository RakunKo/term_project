package com.example.term_project.ui

import MainViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.R
import com.example.term_project.data.entity.Diary
import com.example.term_project.data.entity.UserInfo
import com.example.term_project.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(binding.root)

        val spf = getSharedPreferences("userInfo", MODE_PRIVATE)

        mainViewModel.getAllDiary(spf.getString("uid", "")!!)
        mainViewModel.getAllNote(spf.getString("uid", "")!!)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, MainFragment())
            .commit()

        clickListener()
    }

    override fun onResume() {
        super.onResume()
        val spf = getSharedPreferences("userInfo", MODE_PRIVATE)
        mainViewModel.getAllDiary(spf.getString("uid", "")!!)
        mainViewModel.getAllNote(spf.getString("uid", "")!!)
    }
    fun openDrawer() {
        binding.mainDl.open()
    }

    private fun clickListener() {
        findViewById<ImageView>(R.id.drawer_cancel_btn).setOnClickListener {
            binding.mainDl.close()
        }
    }
}