package com.graham4.patientsync.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TypicodeRepository {
    private val TAG = "TypicodeRepo"
    private val networkTimeout: Long = 30
    private val BASE_URL = "https://my-json-server.typicode.com/"
    private val service: TypicodeService

    var patientsList = MutableLiveData<List<Patient>>()
    var mutablePulseRecords = MutableLiveData<List<PulseRecord>>()
    var pulseRecords = MutableLiveData<List<PulseRecord>>()

    private var databaseRef: DatabaseReference
    private var databasePatientRef: DatabaseReference
    private var databasePulseRef: DatabaseReference

    init {
        val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(networkTimeout, TimeUnit.SECONDS)
        httpClient.readTimeout(networkTimeout, TimeUnit.SECONDS)
        httpClient.addNetworkInterceptor(loggingInterceptor)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        service = retrofit.create(TypicodeService::class.java)

//        getPatients()
        getPulseRecords()

        // Write a message to the database
        databaseRef = FirebaseDatabase.getInstance().reference
        databasePatientRef = databaseRef.child("patients")
        databasePulseRef = databaseRef.child("pulse_records")
        addPatientDbListener()
    }

    private fun addPatientDbListener() {
        val patientDbListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val patientDbList: MutableList<Patient> = mutableListOf()
                dataSnapshot.children.forEach {
                    val user = it.getValue(Patient::class.java)
                    if (user != null) {
                        patientDbList.add(user)
                    }
                }

                patientsList.value = patientDbList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "Loading Patients - onCancelled(): ", databaseError.toException())
            }
        }

        databasePatientRef.addValueEventListener(patientDbListener)
    }

    private fun addPulseRecordDbListener() {
        val pulseRecordDbListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pulseRecordDbList: MutableList<PulseRecord> = mutableListOf()
                dataSnapshot.children.forEach {
                    val pulseRecord = it.getValue(PulseRecord::class.java)
                    if (pulseRecord != null) {
                        pulseRecordDbList.add(pulseRecord)
                    }
                }

                mutablePulseRecords.value = pulseRecordDbList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "Loading Pulse Records - onCancelled(): ", databaseError.toException())
            }
        }

        databasePulseRef.addValueEventListener(pulseRecordDbListener)
    }


    /**
     * Adds a new patient, and if successful it'll attempt to make a call to add a pulse record too
     */
    fun addNewPatient(firstName: String, lastName: String, pulse: String) {
        val numPatients = (patientsList.value?.size)?.plus(1)
        val patient = Patient(numPatients.toString(), firstName, lastName)

        val call = service.addNewPatient(patient)
        call.enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Error from API: $t")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "addNewPatient() Response: " + response.isSuccessful)
                if (response.isSuccessful && pulse.isNotEmpty()) {
                    addNewPulseRecord(pulse, patient.id)
                }
            }
        })
    }

    /**
     * Add new pulse record
     */
    fun addNewPulseRecord(pulse: String, patientId: String) {
        val numPulseRecords = (pulseRecords.value?.size)?.plus(1)
        val pulseRecord = PulseRecord(numPulseRecords.toString(), pulse, patientId)

        val call = service.addNewPulseRecord(pulseRecord)
        call.enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Error from API: $t")

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "addNewPulseRecord() Response: " + response.isSuccessful)
            }

        })
    }

    fun deletePatient(id: String) {
        val call = service.deletePatient(id)
        call.enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Error from API: $t")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "deletePatient() Response: " + response.isSuccessful)
                getPatients()
            }
        })
    }

    private fun getPatients() {
        val call = service.getPatients()
        call.enqueue(object: Callback<List<Patient>> {
            override fun onFailure(call: Call<List<Patient>>, t: Throwable) {
                Log.e(TAG, "Error from API: $t")
            }

            override fun onResponse(call: Call<List<Patient>>, response: Response<List<Patient>>) {
                Log.d(TAG, "received patients.")
                patientsList.value = response.body()
            }
        })
    }

    /**
     * Get all pulse records
     */
    private fun getPulseRecords() {
        val call = service.getPulseRecords()
        call.enqueue(object: Callback<List<PulseRecord>> {
            override fun onFailure(call: Call<List<PulseRecord>>, t: Throwable) {
                Log.e(TAG, "Error from API: $t")
            }

            override fun onResponse(
                call: Call<List<PulseRecord>>,
                response: Response<List<PulseRecord>>
            ) {
                Log.d(TAG, "received pulse records.")
                pulseRecords.value = response.body()!!
            }
        })
    }

    /**
     * Get pulse records by patient ID.
     */
    fun getPulseRecordsByPatientId(patientId: String): MutableLiveData<List<PulseRecord>> {
        val call = service.getPulseRecords()

        call.enqueue(object: Callback<List<PulseRecord>>{
            override fun onFailure(call: Call<List<PulseRecord>>, t: Throwable) {
                Log.e(TAG, "Error from API: $t")
            }

            override fun onResponse(
                call: Call<List<PulseRecord>>,
                response: Response<List<PulseRecord>>
            ) {
                val patientPulseRecords = arrayListOf<PulseRecord>()
                val records = response.body()
                records?.forEach {
                    if(it.patientId == patientId) {
                        patientPulseRecords.add(it)
                    }
                }

                mutablePulseRecords.value = patientPulseRecords
            }

        })

        return mutablePulseRecords
    }
}