package com.hapataka.questwalk.data.resource

import com.hapataka.questwalk.R
import com.hapataka.questwalk.ui.record.model.RecordItem.Achievement
import com.hapataka.questwalk.ui.record.model.RecordItem.Header

object Achievements {
    val list = listOf(
        Header("달성한 업적"),
        Achievement(R.drawable.dummy_achieve_001, "Test001", "1번 업적 테스트용, 업적 설명이 들어간다."),
        Achievement(R.drawable.dummy_achieve_002, "Test002", "2번 업적 테스트용, 업적 설명이 들어간다."),
        Achievement(R.drawable.dummy_achieve_003, "Test003", "3번 업적 테스트용, 업적 설명이 들어간다.")
    )
}