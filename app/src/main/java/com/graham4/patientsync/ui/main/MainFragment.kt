package com.graham4.patientsync.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.graham4.patientsync.R
import com.graham4.patientsync.repository.models.Patient
import com.graham4.patientsync.repository.models.PulseRecord

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: PatientListAdapter? =
        PatientListAdapter { patient: Patient, delPatient: Boolean -> handlePatientListClicked(patient, delPatient) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        val recyclerView = view.findViewById(R.id.recyclerview) as RecyclerView
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )

        val addPatientButton = view.findViewById(R.id.button_add_patient) as Button
        addPatientButton.setOnClickListener{showAddPatient()}

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.getPatients().observe(viewLifecycleOwner, Observer<List<Patient>> { data ->
            adapter?.updatePatientData(data)
        })

        viewModel.getPulseRecords().observe(viewLifecycleOwner, Observer<List<PulseRecord>> { data ->
            adapter?.updatePulseRecordData(data)
        })
    }

    /**
     * Function to show the patient details screen
     */
    private fun showPatientDetails(patient: Patient) {
        parentFragmentManager.beginTransaction()
            .addToBackStack("DetailsFragment")
            .replace(R.id.container, DetailsFragment.newInstance(patient))
            .commit()
    }

    /**
     * Function to handle know if the user clicked the list item or delete button.
     */
    private fun handlePatientListClicked(patient: Patient, delPatient: Boolean) {
        if (delPatient) {
            deletePatient(patient)
        } else {
            showPatientDetails(patient)
        }
    }

    /**
     * Function to show the add patient screen.
     */
    private fun showAddPatient() {
        parentFragmentManager.beginTransaction()
            .addToBackStack("AddPatientFragment")
            .replace(R.id.container, AddPatientFragment.newInstance())
            .commit()
    }

    /**
     * Function to delete a patient.
     */
    private fun deletePatient(patient: Patient) {
        Log.d("MainFrag", "delete patient...")
        viewModel.deletePatient(patient)
    }
}
