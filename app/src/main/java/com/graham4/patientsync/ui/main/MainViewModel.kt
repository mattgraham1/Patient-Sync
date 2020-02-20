package com.graham4.patientsync.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graham4.patientsync.repository.TypicodeRepository
import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord

class MainViewModel : ViewModel() {
    private val patientRepository: TypicodeRepository

    init {
        patientRepository = TypicodeRepository()
    }

    fun getPatients(): MutableLiveData<List<Patient>> {
        return patientRepository.patientsList
    }

    fun getPatientPulseRecords(patientId: String): MutableLiveData<List<PulseRecord>> {
        return patientRepository.getPulseRecordsByPatientId(patientId)
    }

    fun addNewPatient(firstName: String, lastName: String, pulse: String) {
        patientRepository.addNewPatient(firstName, lastName, pulse)
    }

    fun addPatientPulse(pulse: String, patientId: String) {
        patientRepository.addNewPulseRecord(pulse, patientId)
    }

    fun deletePatient(patient: Patient) {
        patientRepository.deletePatient(patient.id)
    }
}
