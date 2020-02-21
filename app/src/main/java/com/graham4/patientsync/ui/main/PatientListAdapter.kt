package com.graham4.patientsync.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.graham4.patientsync.R
import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord

class PatientListAdapter(val listener: (Patient, Boolean) -> Unit) : RecyclerView.Adapter<PatientListAdapter.ViewHolder>() {
    private val TAG = "PatientListAdapter"
    private var patientData: List<Patient> = emptyList()
    private var pulseRecordData: List<PulseRecord> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.patient_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return patientData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.firstName.text = patientData[position].firstName
        holder.lastName.text = patientData[position].lastName
        holder.deleteButton.setOnClickListener { listener (patientData[position], true) }
        holder.itemView.setOnClickListener{ listener(patientData[position], false) }

        // Find most recent pulse per patient
        val patient = patientData[position]
        pulseRecordData.forEach {pulseRecord ->
            if (pulseRecord.patientId == patient.key) {
                holder.recentPulse.text = pulseRecord.pulse
            }
        }
    }

    /**
     * Function to update adapter patient data.
     */
    fun updatePatientData(data: List<Patient>) {
        patientData = data
        notifyDataSetChanged()
    }

    /**
     * Function to update adapter pulse record data.
     */
    fun updatePulseRecordData(records: List<PulseRecord>) {
        pulseRecordData = records
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var firstName: TextView = itemView.findViewById(R.id.textView_firstname)
        var lastName: TextView = itemView.findViewById(R.id.textView_lastname)
        var deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)
        var recentPulse: TextView = itemView.findViewById(R.id.textView_pulse)
    }
}