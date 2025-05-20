package com.example.learningenglish.data.model

data class OcrResponse(
    val contentId: String,         // 업로드된 콘텐츠의 ID
    val text: String,              // 추출된 전체 텍스트
    val sentences: List<String>    // 문장 단위로 나눈 텍스트 (선택적)
)
