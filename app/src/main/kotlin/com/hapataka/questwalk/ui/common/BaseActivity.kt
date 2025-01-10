package com.hapataka.questwalk.ui.common

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

open class BaseActivity<VB : ViewBinding>(private val inflate: Inflate<VB>) : AppCompatActivity() {
    private var _binding: VB? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDefault()
    }

    private fun setupDefault() {
        enableEdgeToEdge()
        _binding = inflate.invoke(layoutInflater, null, false)
        setContentView(binding.root)
    }
}