package com.hapataka.questwalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}