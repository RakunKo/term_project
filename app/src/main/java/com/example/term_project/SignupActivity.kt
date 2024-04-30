package com.example.term_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.term_project.databinding.ActivityLoginBinding
import com.example.term_project.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        binding.signupBackBtn.setOnClickListener {
            finish()
        }
        binding.loginSignupBtn.setOnClickListener {
            val email = binding.signupIdEt.text.toString()
            val pwd = binding.signupPwdEt.text.toString()
            val pwdcon = binding.signupConfirmPwdEt.text.toString()
            val name = binding.signupNameEt.text.toString()
            val info = binding.signupInfoEt.text.toString()

            //예외처리 나중에
            auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        Toast.makeText(this, "회원가입 실패1", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "회원가입 실패2", Toast.LENGTH_SHORT).show()
                }
        }
    }
}