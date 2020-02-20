package com.graham4.patientsync.repository

import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TypicodeService {

    @GET("mattgraham1/PatientSync/patients")
    fun getPatients() : Call<List<Patient>>

    @GET("mattgraham1/PatientSync/pulse_records")
    fun getPulseRecords(): Call<List<PulseRecord>>

    @DELETE("mattgraham1/PatientSync/patients/{id}")
    fun deletePatient(@Path("id") id: String): Call<ResponseBody>

    @POST("mattgraham1/PatientSync/patients")
    fun addNewPatient(@Body patient: Patient): Call<ResponseBody>

    @POST("mattgraham1/PatientSync/pulse_records")
    fun addNewPulseRecord(@Body pulseRecord: PulseRecord): Call<ResponseBody>
}