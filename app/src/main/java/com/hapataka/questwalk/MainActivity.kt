package com.hapataka.questwalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hapataka.questwalk.databinding.ActivityMainBinding
import com.hapataka.questwalk.ui.quest.QuestFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, QuestFragment())
            .commit()
    }
}