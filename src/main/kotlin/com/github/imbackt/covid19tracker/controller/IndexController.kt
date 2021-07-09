package com.github.imbackt.covid19tracker.controller

import com.github.imbackt.covid19tracker.service.DataService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController(val dataService: DataService) {

    @GetMapping("/")
    fun index(model: Model): String {
        model["title"] = "COVID-19 Tracker"
        model["data"] = dataService.getData()
        model["totalConfirmed"] = dataService.getCurrentTotalConfirmed()
        return "index"
    }
}