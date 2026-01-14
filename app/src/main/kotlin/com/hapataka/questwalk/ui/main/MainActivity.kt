package com.hapataka.questwalk.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.ActivityMainBinding
import com.hapataka.questwalk.util.LoadingDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navHost by lazy { supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment }
    val navController by lazy { navHost.navController }
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setObserver()
        initOpenCv()
    }


    private fun setObserver() {
        mainViewModel.snackBarMsg.observe(this) {
            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
        }
        mainViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                LoadingDialogFragment().show(supportFragmentManager, "loadingDialog")
            } else {
                val loadingFragment =
                    supportFragmentManager.findFragmentByTag("loadingDialog") as? LoadingDialogFragment
                loadingFragment?.dismiss()
            }
        }
    }

    private fun initOpenCv() {
        OpenCVLoader.initDebug()
    }
}