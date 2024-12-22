package com.example.myapplication.Data

data class Vezba(
    val Id: Int,
    val SlikaVezbe: String?,
    val KlipVezbe: String,
    val Naziv: String,
    val Pol: String,
    val MišićnaParticijaId: Int,
    val PrioritetId: Int,
    val Mišić: List<String>,
    val InstrukcijeVezbe: String,
    val OpasnaVezba: Boolean,
    val TezinaVezbe: Int,
    val MuskaTezina: Int,
    val ZenskaTezina: Int,
    val Radjeno: Boolean
)

data class VezbeResponse(
    val Vezbe: List<Vezba>
)


