package com.hcmus.tenderus.fake

import com.hcmus.tenderus.model.Account

object FakeDataSource {
    val accountList = listOf(
        Account(username = "bao", role = "admin"),
        Account(username = "phong", role = "admin"),
        Account(username = "thuy", role = "admin"),
        Account(username = "vu", role = "admin"),
        Account(username = "y", role = "admin"),
    )
}