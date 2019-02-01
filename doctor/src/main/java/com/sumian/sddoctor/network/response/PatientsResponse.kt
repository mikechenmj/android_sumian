package com.sumian.sddoctor.network.response

import com.sumian.sddoctor.patient.bean.Patient

data class PatientsResponse(var data: ArrayList<Patient>, var meta: Meta)