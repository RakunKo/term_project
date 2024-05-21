package com.example.term_project.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.example.term_project.R
import com.example.term_project.databinding.ToastLayoutBinding

object CustomToast {
    fun createToast(context: Context, message: String, yOffset: Int, isOK: Boolean) {
        val inflater = LayoutInflater.from(context)
        val binding: ToastLayoutBinding = ToastLayoutBinding.inflate(inflater)

        binding.toastMessageTv.text = message
        if (isOK) binding.toastImg.setImageResource(R.drawable.toast_confirm)
        else binding.toastImg.setImageResource(R.drawable.toast_error)

        Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, yOffset)
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }.show()
    }
}