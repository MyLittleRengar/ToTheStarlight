package com.project.tothestarlight

fun getLocationCode(location: String): String? {
    val locations = mapOf(
        "서울, 인천, 경기도" to "11B00000",
        "강원도영서" to "11D10000",
        "강원도영동" to "11D20000",
        "대전, 세종, 충청남도" to "11C20000",
        "충청북도" to "11C10000",
        "광주, 전라남도" to "11F20000",
        "전북자치도" to "11F10000",
        "대구, 경상북도" to "11H10000",
        "부산, 울산, 경상남도" to "11H20000",
        "제주도" to "11G00000"
    )
    return locations[location]
}