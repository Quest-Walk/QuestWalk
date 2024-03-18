package com.hapataka.questwalk.data.resource

import com.hapataka.questwalk.ui.record.model.RecordItem.AchieveItem
import com.hapataka.questwalk.ui.record.model.RecordItem.Header

object Achievements {
    val list = listOf(
        Header("달성한 업적"),
        AchieveItem(0, "https://firebasestorage.googleapis.com/v0/b/quest-walk-d261b.appspot.com/o/Achievement%2Fic_achieve_001.png?alt=media&token=c43d4bda-2a2f-49e0-94b3-ddc350c774dd", "Test001", "처음으로 퀘스트를 성공했어요."),
        AchieveItem(1, "https://firebasestorage.googleapis.com/v0/b/quest-walk-d261b.appspot.com/o/Achievement%2Fic_achieve_002.png?alt=media&token=e32ce664-0ef4-4660-8e7e-3a5d5666788d", "Test002", "2번 업적 테스트용, 업적 설명이 들어간다."),
    )
}