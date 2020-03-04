package com.graham4.patientsync.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.graham4.patientsync.R
import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord

class DetailsFragment constructor(var patient: Patient) : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var adapter: PulseListAdapter =
            PulseListAdapter{ pulseRecord: PulseRecord -> deletePulseRecord(pulseRecord) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: ProgressBar

    companion object {
        fun newInstance(patient: Patient) = DetailsFragment(patient)
    }

    constructor() : this(Patient())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.details_fragment, container, false)

        val firstName: TextView = view.findViewById(R.id.textView_firstname) as TextView
        firstName.text = patient.firstName
        val lastName: TextView = view.findViewById(R.id.textView_lastname) as TextView
        lastName.text = patient.lastName

        val addPulseButton = view.findViewById(R.id.button_add_pulse) as Button
        addPulseButton.setOnClickListener {
            addCurrentPulse()
        }

        spinner = view.findViewById(R.id.spinner_details_view) as ProgressBar
        recyclerView = view.findViewById(R.id.recyclerview_pulse_records) as RecyclerView

        setSpinnerVisible(true)

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        // Added to ensure data is retained during screen rotate
        retainInstance = true

        viewModel.getPulseRecords().observe(viewLifecycleOwner, Observer<List<PulseRecord>> { data ->
            setSpinnerVisible(false)
            adapter.updatePulseRecordedData(data, patient.key)
        })
    }

    /**
     * Function to show or hide spinner.
     */
    private fun setSpinnerVisible(visible: Boolean) {
        if (visible) {
            spinner.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            spinner.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    /**
     * Function to add current pulse to patient.
     */
    private fun addCurrentPulse() {
        val addPulseEditText = EditText(activity)

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add Pulse")
        builder.setMessage("Add Patient's Current Pulse")
        builder.setNegativeButton("Cancel") {dialog, which ->
            // do nothing, dismiss
        }
        builder.setPositiveButton("Submit") {dialog, which ->
            Log.d("DetailsFrag", "addCurrentPulse() submit button clicked, pulse entered: " + addPulseEditText.text)
            viewModel.addPatientPulse(addPulseEditText.text.toString(), patient)
        }

        addPulseEditText.hint = "Enter Current Pulse"
        addPulseEditText.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(addPulseEditText)

        builder.show()
    }

    /**
     * Function to delete pulse record
     */
    private fun deletePulseRecord(pulseRecord: PulseRecord) {
        viewModel.deletePulseRecord(pulseRecord)
    }
}