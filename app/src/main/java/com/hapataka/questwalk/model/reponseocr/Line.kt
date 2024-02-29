package com.hapataka.questwalk.model.reponseocr

data class Line(
    val MaxHeight: Int,
    val MinTop: Int,
    val Words: List<Word>
)