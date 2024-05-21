package com.example.term_project.ui

import MainViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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

        btmNavi()
    }

    override fun onResume() {
        super.onResume()
        val spf = getSharedPreferences("userInfo", MODE_PRIVATE)
        mainViewModel.getAllDiary(spf.getString("uid", "")!!)
    }

    private fun btmNavi() {
        binding.mainBtmNavi.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(MainFragment())
                    true
                }
                R.id.info -> {
                    replaceFragment(MyprofileFragment())
                    true
                }
                R.id.map -> {
                    replaceFragment(CollectionFragment())
                    true
                }
                // 필요에 따라 다른 항목 처리
                else -> false
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commit()
    }
}