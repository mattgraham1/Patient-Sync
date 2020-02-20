package com.graham4.patientsync.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.graham4.patientsync.R

class AddPatientFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    companion object {
        fun newInstance() = AddPatientFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.add_patient_fragment, container, false)

        val firstNameEdit = view.findViewById(R.id.edittext_first_name) as EditText
        val lastNameEdit = view.findViewById(R.id.edittext_last_name) as EditText
        val pulseEdit = view.findViewById(R.id.edittext_pulse) as EditText

        val submit = view.findViewById(R.id.button_submit) as Button
        submit.setOnClickListener {
            //TODO: validate fields before submitting, maybe show a toast or something
            addNewPatient(firstNameEdit.text.toString(), lastNameEdit.text.toString(), pulseEdit.text.toString())
            activity?.supportFragmentManager?.popBackStack()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun addNewPatient(firstName: String, lastName: String, pulse: String) {
        Log.d("AddPatientFrag", "Submit button clicked, first name: " + firstName
                + ", last name: " + lastName + ", pulse: " + pulse)
        viewModel.addNewPatient(firstName,lastName, pulse)
    }
}