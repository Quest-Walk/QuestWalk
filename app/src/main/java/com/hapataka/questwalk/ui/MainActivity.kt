package com.hapataka.questwalk.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.firebase.repository.AchieveStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.data.resource.Achievement
import com.hapataka.questwalk.data.resource.Resource
import com.hapataka.questwalk.databinding.ActivityMainBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.ui.adapter.AchieveAdapter
import com.hapataka.questwalk.ui.adapter.AchieveAdapter.ItemClick
import com.hapataka.questwalk.ui.adapter.QuestAdapter
import com.hapataka.questwalk.ui.adapter.QuestAdapter.QuestItemClick
import kotlinx.coroutines.launch

const val TAG = "test_tag"

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val authRepo by lazy { AuthRepositoryImpl() }
    private val imageRepo by lazy { ImageRepositoryImpl() }
    private val achieveRepo by lazy { AchieveStackRepositoryImpl() }
    private val questRepo by lazy { QuestStackRepositoryImpl() }
    private val userRepo by lazy { UserRepositoryImpl() }
    private val pref by lazy { this.getSharedPreferences("user", 0) }
    private var currentAchieveId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initIdPw()
        testAuth()
        testStoragy()
        testFierstore()
    }

    private fun initIdPw() {
        pref.also {
            binding.etId.setText(it.getString("id", ""))
            binding.etPw.setText(it.getString("pw", ""))
        }
    }

    private fun testAuth() {
        testSignUp()
        testLogin()
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                authRepo.logout()
            }
        }
    }

    private fun testStoragy() {
        changePicture()
        uploadPicture()
    }

    private fun testFierstore() {
        testAchieveRepo()
        testQuestRepo()
        testUserRepo()
    }

    private fun testUserRepo() {
        checkUser()
        setInfo()
        getHistory()
        getResult()
        getAchieve()
    }

    private fun getHistory() {
        binding.btnCheckHistory.setOnClickListener {
            lifecycleScope.launch {
                if (authRepo.getCurrentUserUid().isEmpty()) {
                    binding.tvUserInfoResult.isFail("아이디 없음", true)
                    return@launch
                }
                val history = userRepo.getUserHistory(authRepo.getCurrentUserUid())
                binding.tvUserInfo.text = history.joinToString("\n\n======================\n\n")
            }
        }
    }

    private fun getResult() {
        binding.btnCheckResult.setOnClickListener {
            lifecycleScope.launch {
                if (authRepo.getCurrentUserUid().isEmpty()) {
                    binding.tvUserInfoResult.isFail("아이디 없음", true)
                    return@launch
                }
                val result = userRepo.getResultHistory(authRepo.getCurrentUserUid())
                binding.tvUserInfo.text = result.joinToString("\n\n======================\n\n")
            }
        }
    }

    private fun getAchieve() {
        binding.btnCheckHistoryAchieve.setOnClickListener {
            lifecycleScope.launch {
                if (authRepo.getCurrentUserUid().isEmpty()) {
                    binding.tvUserInfoResult.isFail("아이디 없음", true)
                    return@launch
                }
                val achieve = userRepo.getAchieveHistory(authRepo.getCurrentUserUid())
                binding.tvUserInfo.text = achieve.joinToString("\n\n======================\n\n")
            }
        }
    }
    private fun setInfo() {
        binding.btnSetUser.setOnClickListener {
            lifecycleScope.launch {
                val uid = authRepo.getCurrentUserUid()
                val result = HistoryEntity.ResultEntity(
                    "2023.12.31",
                    "오스트랄로피테쿠스",
                    "12:23",
                    12.4f,
                    2345,
                    false,
                    listOf(11.23f, 12.22f, 13.22f),
                    listOf(132.4f, 133.2f, 134.2f),
                    12.22f,
                    133.2f,
                    imageUri.toString(),
                )

                val achieve = HistoryEntity.AchievementEntity(
                    "2023.12.31",
                    1
                )

                userRepo.setInfo(uid,result)
                userRepo.setInfo(uid,achieve)
            }
        }
    }

    private fun checkUser() {
        binding.btnCheckUserInfo.setOnClickListener {
            lifecycleScope.launch {
                val uid = authRepo.getCurrentUserUid()

                Log.i(TAG, "uid: $uid")

                if (uid.isEmpty()) {
                    binding.tvUserInfoResult.isFail("아이디 없음", true)
                    return@launch
                }
                val userInfo = userRepo.getInfo(uid)

                binding.tvUserInfo.text = userInfo.toString()
            }
        }
    }

    private val achieveAdapter by lazy { AchieveAdapter(this) }
    private val questAdapter by lazy { QuestAdapter(this) }

    private fun testQuestRepo() {
        initQuestRecyclerView()
        binding.btnCheckQuest.setOnClickListener {
            lifecycleScope.launch {
                questAdapter.submitList(questRepo.getAllItems())
            }
        }
    }

    private fun testAchieveRepo() {
        initAchieveRecycerView()
        initItemClick()
        initCheckButton()
    }

    private fun initQuestRecyclerView() {
        with(binding.rvQuest) {
            adapter = questAdapter
            layoutManager = LinearLayoutManager(context)
        }
        lifecycleScope.launch {
            questAdapter.submitList(questRepo.getAllItems())
        }
        initQuestClick()
    }

    private fun initQuestClick() {
        object : QuestItemClick {
            override fun onClick(item: QuestStackEntity) {
                lifecycleScope.launch {
                    val currentUid = authRepo.getCurrentUserUid()

                    if (currentUri == null) {
                        binding.tvQuestResult.isFail("현재 Uri가 없음", true)
                        return@launch
                    }

                    questRepo.updateQuest(item.keyWord, currentUid, currentUri.toString())
                }
            }
        }.also { questAdapter.onClick = it }
    }

    private fun initAchieveRecycerView() {
        with(binding.rvAchieve) {
            adapter = achieveAdapter
            layoutManager = LinearLayoutManager(context)
        }
        achieveAdapter.submitList(Resource.achieveList.toList())
    }

    private fun initItemClick() {
        object : ItemClick {
            override fun onClick(item: Achievement) {
                lifecycleScope.launch {
                    achieveRepo.countUpAchieveStack(item.achieveId)
                    currentAchieveId = item.achieveId
                    binding.tvCurrentId.text = currentAchieveId.toString()
                }
            }
        }.also { achieveAdapter.onClick = it }
    }

    private fun initCheckButton() {
        binding.btnCheckAchieve.setOnClickListener {
            lifecycleScope.launch {
                val stack = achieveRepo.getAchieveStateById(currentAchieveId)

                binding.tvAchieveResult.text = "count: ${stack.count}"
            }
        }
    }

    var currentUri: Uri? = null
    private fun uploadPicture() {
        binding.btnUploadPic.setOnClickListener {
            lifecycleScope.launch {
                val currentUid = authRepo.getCurrentUserUid()

                if (currentUid.isEmpty()) {
                    binding.tvImageUrl.setText("uid 없음")
                    return@launch
                }

                if (imageUri == null) {
                    binding.tvImageUrl.setText("url 없음")
                    return@launch
                }
                currentUri = imageRepo.setImage(imageUri!!, currentUid)

                binding.tvImageUrl.setText(currentUri.toString())
            }
        }
    }

    private var imageUri: Uri? = null
    private fun changePicture() {
        val imageResultLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { result ->
                if (result != null) {
                    this.contentResolver.takePersistableUriPermission(
                        result,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    binding.ivPic.load(result)
                }
                imageUri = result
            }
        binding.btnChangePic.setOnClickListener {
            imageResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun testSignUp() {
        with(binding) {
            binding.btnSignUp.setOnClickListener {
                val id = etId.text.toString()
                val pw = etPw.text.toString()

                if (id.isEmpty() && pw.isEmpty()) {
                    tvResult.setText("값 입력 해라")
                    tvResult.setTextColor(resources.getColor(R.color.red))
                    return@setOnClickListener
                }
                checkSignUp(id, pw)
            }
        }
    }

    private fun checkSignUp(id: String, pw: String) {
        lifecycleScope.launch {
            val tvResult = binding.tvResult

            authRepo.registerByEmailAndPw(id, pw) { task ->
                if (task.isSuccessful) {
                    tvResult.isFail("회원가입 성공", false)
                    pref.edit().run {
                        putString("id", id)
                        putString("pw", pw)
                        apply()
                    }
                    return@registerByEmailAndPw
                }
                tvResult.isFail("회원가입 실패", true)
            }
        }
    }

    private fun testLogin() {
        with(binding) {
            btnLogin.setOnClickListener {
                val id = etId.text.toString()
                val pw = etPw.text.toString()

                if (etId.text.isEmpty() && etPw.text.isEmpty()) {
                    tvResult.isFail("값 입력 해라", false)
                    return@setOnClickListener
                }
                checkLogin(id, pw)
            }
        }
    }

    private fun checkLogin(id: String, pw: String) {
        lifecycleScope.launch {
            authRepo.loginByEmailAndPw(id, pw) { task ->
                if (task.isSuccessful) {
                    binding.tvResult.isFail("로그인 성공", false)
                    showUid()
                    return@loginByEmailAndPw
                }
                binding.tvResult.isFail("로그인 실패", true)
            }
        }
    }

    private fun showUid() {
        lifecycleScope.launch {
            val currentUid = authRepo.getCurrentUserUid()

            binding.tvUid.text = currentUid
        }
    }

    private fun TextView.isFail(msg: String, result: Boolean) {
        setText(msg)
        setTextColor(if (result) resources.getColor(R.color.red) else resources.getColor(R.color.green))
    }
}
