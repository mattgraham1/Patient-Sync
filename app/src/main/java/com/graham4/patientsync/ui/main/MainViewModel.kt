package com.graham4.patientsync.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graham4.patientsync.repository.DataRepository
import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord

class MainViewModel : ViewModel() {
    fun getPatients(): MutableLiveData<List<Patient>> {
        return DataRepository.patientsListFromDb
    }

    fun getPulseRecords(): MutableLiveData<List<PulseRecord>> {
        return DataRepository.mutablePatientPulseRecords
    }

    fun deletePulseRecord(pulseRecord: PulseRecord) {
        DataRepository.deletePulseRecord(pulseRecord.key)
    }

    fun addNewPatient(firstName: String, lastName: String, pulse: String) {
        DataRepository.addNewPatientToDb(firstName, lastName, pulse)
    }

    fun addPatientPulse(pulse: String, patient: Patient) {
        DataRepository.addNewPulseRecord(pulse, patient)
    }

    fun deletePatient(patient: Patient) {
        DataRepository.deletePatient(patient.key)
    }
}
