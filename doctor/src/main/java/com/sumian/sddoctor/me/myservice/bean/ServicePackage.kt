package com.sumian.sddoctor.me.myservice.bean

data class ServicePackage(
        val id: Int,
        val service_id: Int,
        val name: String,
        val introduction: String,
        val default_price: Int,
        val service_length: Int,
        val service_length_unit: Int,
        val enable: Int,
        val sold_by_doctor: Int,
        var packages: Packages
)