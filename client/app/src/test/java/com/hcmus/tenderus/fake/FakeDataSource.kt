package com.hcmus.tenderus.fake

import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.Status
import java.time.LocalDateTime

object FakeDataSource {
    val reportList = listOf(
        Report(
            id = "000001",
            reporter = "user001",
            reported = "user002",
            date = LocalDateTime.now().toString(),
            status = Status.PENDING,
            message = "しかのこのこのここしたんたん"
        ),
        Report(
            id = "000001",
            reporter = "user001",
            reported = "user002",
            date = LocalDateTime.now().toString(),
            status = Status.PENDING,
            message = "しかのこのこのここしたんたん"
        ),
    )
}