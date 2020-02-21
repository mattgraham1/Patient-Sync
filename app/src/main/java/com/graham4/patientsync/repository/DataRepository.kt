package com.graham4.patientsync.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord

object DataRepository {
    private val TAG = "TypicodeRepo"

    var patientsListFromDb = MutableLiveData<List<Patient>>()

    var mutablePatientPulseRecords = MutableLiveData<List<PulseRecord>>()
    var allPulseRecordsFromDb = mutableListOf<PulseRecord>()

    private var databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var databasePatientRef: DatabaseReference
    private var databasePulseRef: DatabaseReference

    init {
        // Set database references and add listeners
        databasePatientRef = databaseRef.child("patients")
        addPatientDbListener()
        databasePulseRef = databaseRef.child("pulse_records")
        addPulseRecordDbListener()
    }

    /**
     * Function to add a listener for patients to the firebase database.
     */
    private fun addPatientDbListener() {
        val patientDbListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val patientDbList: MutableList<Patient> = mutableListOf()
                dataSnapshot.children.forEach {
                    var user = it.getValue(Patient::class.java)
                    user?.key = it.key.toString()
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

    /**
     * Function to add a listener for pulse records to the firebase database.
     */
    private fun addPulseRecordDbListener() {
        val pulseRecordDbListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allPulseRecordsFromDb.clear()
                dataSnapshot.children.forEach {
                    var pulseRecord = it.getValue(PulseRecord::class.java)
                    pulseRecord?.key = it.key.toString()
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
     * Function to add a new patient, and if successful it'll attempt to make a call to add
     * a pulse record too.
     */
    fun addNewPatientToDb(firstName: String, lastName: String, pulse: String) {
        val patient = Patient("", firstName, lastName)

        // Add patient
        val patientKey = databasePatientRef.push().key
        databasePatientRef.child(patientKey.toString()).setValue(patient)

        // Add Pulse
        if (pulse.isNotEmpty()) {
            val pulseRecord = PulseRecord("", pulse, patientKey.toString())
            databasePulseRef.push().setValue(pulseRecord)
        }
    }

    /**
     * Add new pulse record
     */
    fun addNewPulseRecord(pulse: String, patient: Patient) {
        val pulseRecord = PulseRecord("", pulse, patient.key)

        databasePulseRef.push().setValue(pulseRecord)
    }

    /**
     * Delete Patient
     */
    fun deletePatient(id: String) {
        databasePatientRef.child(id).removeValue()
    }

    /**
     * Delete Pulse Record
     */
    fun deletePulseRecord(id: String) {
        databasePulseRef.child(id).removeValue()
    }
}