package com.hcmus.tenderus.fake

import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.Role

object FakeDataSource {
    val accountList = listOf(
        Account(username = "bao", role = Role.ADMIN, email = "hehe@hehe", phone = "123"),
        Account(username = "phong", role = Role.ADMIN, email = "hehe@hehe", phone = "123"),
        Account(username = "thuy", role = Role.ADMIN, email = "hehe@hehe", phone = "123"),
        Account(username = "vu", role = Role.ADMIN, email = "hehe@hehe", phone = "123"),
        Account(username = "y", role = Role.ADMIN, email = "hehe@hehe", phone = "123"),
    )
}