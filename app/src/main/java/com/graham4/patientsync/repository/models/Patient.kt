package com.graham4.patientsync.repository.models

import com.google.gson.annotations.SerializedName

data class Patient(
    @SerializedName("id") val id : String = "",
    @SerializedName("first_name") val firstName : String = "",
    @SerializedName("last_name") val lastName : String = ""
)