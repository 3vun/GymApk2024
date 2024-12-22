package com.example.myapplication.Data

data class Misic(
    val Id: Int,
    val SlikaMisica: String?,
    val Naziv: String,
    val MišićnaParticijaId: List<Int>,
    val Opis: String,
)

data class MisicResponse(
    val Misic: List<Vezba>
)

