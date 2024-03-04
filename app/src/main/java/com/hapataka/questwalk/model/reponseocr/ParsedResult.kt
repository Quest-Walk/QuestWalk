package com.hapataka.questwalk.model.reponseocr

data class ParsedResult(
    val ErrorDetails: String,
    val ErrorMessage: String,
    val FileParseExitCode: Int,
    val ParsedText: String,
    val TextOverlay: TextOverlay
)