package com.hapataka.questwalk.model.reponseocr

data class TextOverlay(
    val HasOverlay: Boolean,
    val Lines: List<Line>,
    val Message: Any
)