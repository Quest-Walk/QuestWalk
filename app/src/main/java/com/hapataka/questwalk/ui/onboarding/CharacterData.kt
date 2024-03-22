package com.hapataka.questwalk.ui.onboarding

import com.hapataka.questwalk.R

data class CharacterData(
    val img : Int ,
    val name : String ,
    val num : Int
) {
    companion object {
        val characterList = listOf(
            CharacterData(R.drawable.character_01,"여행하는 곰돌이",1),
//            CharacterData(R.drawable.img_dummy_charcter,"???",2)
        )
    }
}


interface OnCharacterSelectedListener {
    fun onCharacterSelected(characterData: CharacterData)
}