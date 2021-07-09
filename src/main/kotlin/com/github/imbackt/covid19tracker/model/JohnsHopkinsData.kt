package com.github.imbackt.covid19tracker.model

import java.time.LocalDate
import java.util.*

data class JohnsHopkinsData(
        val province: String,
        val country: String,
        val lat: Double,
        val long: Double,
        val stats: Map<LocalDate, Int>,
        val totalConfirmed: Int
)