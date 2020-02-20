package com.graham4.patientsync.repository.models

import com.google.gson.annotations.SerializedName

data class PulseRecord (
    @SerializedName("id") val id : String = "",
    @SerializedName("pulse") val pulse : String = "",
    @SerializedName("patientId") val patientId : String = ""
)