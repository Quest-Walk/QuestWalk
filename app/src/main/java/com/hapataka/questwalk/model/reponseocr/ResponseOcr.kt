package com.hapataka.questwalk.model.reponseocr

data class ResponseOcr(
    val ErrorDetails: Any,
    val ErrorMessage: Any,
    val IsErroredOnProcessing: Boolean,
    val OCRExitCode: String,
    val ParsedResults: List<ParsedResult>,
    val ProcessingTimeInMilliseconds: String,
    val SearchablePDFURL: String
)