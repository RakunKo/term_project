package com.example.term_project.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.term_project.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        clickListener()


    }

    private fun clickListener() {
        binding.loginForgetPasswordTv.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        binding.loginLoginBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email : String = binding.loginIdEt.text.toString()
        val pwd : String = binding.loginPwdEt.text.toString()
        val pattern: Pattern = Patterns.EMAIL_ADDRESS

        if (pattern.matcher(email).matches() && pwd != "") {
            auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        saveUid(user!!.uid)
                        Log.d("uid", user!!.uid)
                        startMainActivity()
                    } else {
                        binding.loginError.text = "이메일, 비밀번호를 확인해주세요"
                        binding.loginError.visibility = View.VISIBLE
                    }
                }
        } else {
            binding.loginError.text = "이메일을 확인해주세요"
            binding.loginError.visibility = View.VISIBLE
        }
    }

    private fun startMainActivity() {
        val intent =Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // LoginActivity를 종료하여 뒤로 가기 버튼으로 돌아가지 않도록 함
    }

    private fun saveUid(uid:String) {
        val spf = getSharedPreferences("userInfo", MODE_PRIVATE)
        val editor = spf.edit()
        editor.putString("uid", uid)
        editor.apply()
    }
}