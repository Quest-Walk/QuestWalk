package com.hapataka.questwalk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val authRepo by lazy { AuthRepositoryImpl() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initStartDestination()
    }

    private fun initStartDestination() {
        lifecycleScope.launch {
            val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
            val navController = navHost.navController
            val navGraph = navController.graph
            val currentUser = authRepo.getCurrentUserUid()


            if (currentUser.isNotEmpty()) {
                navGraph.setStartDestination(R.id.frag_home)
                navController.graph = navGraph
                Toast.makeText(this@MainActivity, "로그인된 유저", Toast.LENGTH_SHORT).show()
                return@launch
            }
            navGraph.setStartDestination(R.id.frag_login)
            navController.graph = navGraph
            Toast.makeText(this@MainActivity, "로그인 안함", Toast.LENGTH_SHORT).show()
        }
    }

}