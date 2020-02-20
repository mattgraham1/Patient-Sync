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

object DataRepository {
    private val TAG = "TypicodeRepo"
    private val networkTimeout: Long = 30
    private val BASE_URL = "https://my-json-server.typicode.com/"
    private val service: TypicodeService

    var patientsListFromDb = MutableLiveData<List<Patient>>()

    var mutablePatientPulseRecords = MutableLiveData<List<PulseRecord>>()
    var allPulseRecordsFromDb = mutableListOf<PulseRecord>()

    private var databaseRef: DatabaseReference
    private var databasePatientRef: DatabaseReference
    private var databasePulseRef: DatabaseReference

    init {
        val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        // TODO: REmove service as it's not used anymore. Iterfaces etc etc...
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

        // Set database references and add listeners
        databaseRef = FirebaseDatabase.getInstance().reference
        databasePatientRef = databaseRef.child("patients")
        addPatientDbListener()
        databasePulseRef = databaseRef.child("pulse_records")
        addPulseRecordDbListener()
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

                patientsListFromDb.value = patientDbList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Loading Patients - onCancelled(): ", databaseError.toException())
            }
        }

        databasePatientRef.addValueEventListener(patientDbListener)
    }

    private fun addPulseRecordDbListener() {
        val pulseRecordDbListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allPulseRecordsFromDb.clear()
                dataSnapshot.children.forEach {
                    val pulseRecord = it.getValue(PulseRecord::class.java)
                    if (pulseRecord != null) {
                        allPulseRecordsFromDb.add(pulseRecord)
                    }
                }

                mutablePatientPulseRecords.value = allPulseRecordsFromDb
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Loading Pulse Records - onCancelled(): ", databaseError.toException())
            }
        }

        databasePulseRef.addValueEventListener(pulseRecordDbListener)
    }

    /**
     * Adds a new patient, and if successful it'll attempt to make a call to add a pulse record too
     */
    fun addNewPatientToDb(firstName: String, lastName: String, pulse: String) {
        val numPatients = (patientsListFromDb.value?.size)?.plus(1)
        val patient = Patient(numPatients.toString(), firstName, lastName)

        databasePatientRef.child(numPatients.toString()).setValue(patient)

        //TODO: Update pulse too
    }

    /**
     * Add new pulse record
     */
    fun addNewPulseRecord(pulse: String, patientId: String) {
        val numPulseRecords = (mutablePatientPulseRecords.value?.size)?.plus(1)
        val pulseRecord = PulseRecord(numPulseRecords.toString(), pulse, patientId)

        databasePulseRef.child(numPulseRecords.toString()).setValue(pulseRecord)
    }

    fun deletePatient(id: String) {
        databasePatientRef.child(id).removeValue()
    }

    fun deletePulseRecord(id: String) {
        databasePulseRef.child(id).removeValue()
    }
}