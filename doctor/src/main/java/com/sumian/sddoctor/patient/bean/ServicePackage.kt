package com.sumian.sddoctor.patient.bean

data class ServicePackage(var id: Int,
                          var description: String?,
                          var expired_at: Int,
                          var service: DoctorService?)