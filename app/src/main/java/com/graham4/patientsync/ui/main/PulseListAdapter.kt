package com.graham4.patientsync.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.graham4.patientsync.R
import com.graham4.patientsync.repository.models.PulseRecord

class PulseListAdapter(val listener: (PulseRecord) -> Unit) : RecyclerView.Adapter<PulseListAdapter.ViewHolder>() {
    private var pulseData: List<PulseRecord> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.pulse_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return pulseData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pulseValue.text = pulseData[position].pulse
        holder.deleteButton.setOnClickListener { listener (pulseData[position]) }
    }

    /**
     * Function to update adapter data.
     */
    fun updatePulseRecordedData(data: List<PulseRecord>, patientId: String) {
        pulseData = filterPulseRecordsByPatient(data, patientId)
        notifyDataSetChanged()
    }

    /**
     * Function to filter pulse records per patient.
     */
    private fun filterPulseRecordsByPatient(data: List<PulseRecord>, patientId: String): List<PulseRecord> {
        val filteredPulseRecord = mutableListOf<PulseRecord>()

        data.forEach {
            if(it.patientId == patientId) {
                filteredPulseRecord.add(it)
            }
        }
        return filteredPulseRecord
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pulseValue: TextView = itemView.findViewById(R.id.textview_pulse_value)
        var deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)
    }
}