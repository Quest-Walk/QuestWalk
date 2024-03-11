package com.hapataka.questwalk.data.resource

import com.hapataka.questwalk.R
import com.hapataka.questwalk.ui.record.model.RecordItem.AchieveItem
import com.hapataka.questwalk.ui.record.model.RecordItem.Header

object Achievements {
    val list = listOf(
        Header("달성한 업적"),
        AchieveItem(0, R.drawable.dummy_achieve_001, "Test001", "처음으로 퀘스트를 성공했어요."),
        AchieveItem(1, R.drawable.dummy_achieve_002, "Test002", "2번 업적 테스트용, 업적 설명이 들어간다."),
        AchieveItem(2, R.drawable.dummy_achieve_003, "Test003", "3번 업적 테스트용, 업적 설명이 들어간다.")
    )
}