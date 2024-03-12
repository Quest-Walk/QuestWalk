package com.hapataka.questwalk.ui.mainactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val authRepo by lazy { AuthRepositoryImpl() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStartDestination()
    }

    private fun setStartDestination() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        lifecycleScope.launch {
            val currentUser = authRepo.getCurrentUserUid()

            if (currentUser.isNotEmpty()) {
                navGraph.setStartDestination(R.id.frag_home)
                navController.graph = navGraph
                return@launch
            }
            navGraph.setStartDestination(R.id.frag_login)
            navController.graph = navGraph
        }
    }
}