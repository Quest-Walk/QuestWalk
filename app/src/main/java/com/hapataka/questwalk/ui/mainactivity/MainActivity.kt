package com.hapataka.questwalk.ui.mainactivity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
    private val navHost by lazy { supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment }
    val navController by lazy { navHost.navController }
    private val navGraph by lazy { navController.navInflater.inflate(R.navigation.nav_graph) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStartDestination()
        setFragmentChangeListener()
    }

    private fun setStartDestination() {
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

    private fun setFragmentChangeListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.frag_home) {
                checkPermissions()
            }
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkCamera()
            checkLocation()
        }
    }

    private fun checkCamera() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted.not()) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    return@registerForActivityResult
                }
                showDialog(
                    "Quest Keyword를 사용하려면\n카메라 권한이 필요합니다.",
                ) {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }
        }

        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun checkLocation() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted.not()) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    return@registerForActivityResult
                }

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    return@registerForActivityResult
                }

                showDialog(
                    "Quest Keyword를 사용하려면\n위치 권한이 필요합니다.",
                ) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        }

        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private fun showDialog(msg: String, callback: () -> Unit) {
        val dialog = PermissionDialog(msg, callback)

        dialog.show(supportFragmentManager, "permissionDialog")
    }
}