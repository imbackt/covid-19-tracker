package com.github.imbackt.covid19tracker.service

import com.github.imbackt.covid19tracker.model.JohnsHopkinsData
import org.apache.commons.csv.CSVFormat
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

const val DATA_URL =
    "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv"

@Service
class DataService {

    private var data = arrayListOf<JohnsHopkinsData>()

    @PostConstruct
    @Scheduled(cron = "0 0 12 * * *")
    private fun fetchData() {
        val newData = arrayListOf<JohnsHopkinsData>()
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(URI.create(DATA_URL)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val reader = StringReader(response.body())
        val records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)
        val headers = records.headerNames

        records.forEach {
            val dataMap = mutableMapOf<LocalDate, Int>()
            for (i in 4 until it.size()) {
                val formatter = DateTimeFormatter.ofPattern("M/d/yy")
                dataMap[LocalDate.parse(headers[i], formatter)] = it.get(i).toInt()
            }
            newData.add(
                JohnsHopkinsData(
                    province = it["Province/State"],
                    country = it["Country/Region"],
                    lat = if (it["Lat"].isNotBlank()) it["Lat"].toDouble() else 0.0,
                    long = if (it["Long"].isNotBlank()) it["Long"].toDouble() else 0.0,
                    stats = dataMap,
                    totalConfirmed = it[it.size() - 1].toInt()
                )
            )
        }
        data = newData
    }

    fun getData() = data

    fun getCurrentTotalConfirmed() = data.stream().mapToInt(JohnsHopkinsData::totalConfirmed).sum()
}