package com.hapataka.questwalk.ui

import android.os.Bundle
import android.view.View
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.ActivityLoginBinding
import com.hapataka.questwalk.ui.common.BaseActivity
import com.hapataka.questwalk.util.extentions.setInnerPadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<View>(R.id.main).setInnerPadding()

    }
}