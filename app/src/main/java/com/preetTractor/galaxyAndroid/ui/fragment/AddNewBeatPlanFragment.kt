package com.preetTractor.galaxyAndroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.beatplan.DataBeatPlan
import com.preetTractor.galaxyAndroid.data.team.DataTeamList
import com.preetTractor.galaxyAndroid.databinding.AddNewBeatPlanBinding
import com.preetTractor.galaxyAndroid.helper.Globals.transformIntoDatePicker
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.ui.activity.BeatPlanActivity


class AddNewBeatPlanFragment : Fragment() {
    lateinit var binding: AddNewBeatPlanBinding
    lateinit var viewModel: MainViewModel
    lateinit var salesEmployeeAdapter : ArrayAdapter<DataTeamList>
    var bpAdapter : ArrayAdapter<String>? =null
    var beatPlanList = ArrayList<DataBeatPlan>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = AddNewBeatPlanBinding.inflate(layoutInflater)
        viewModel = (activity as BeatPlanActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PrefsByShubh.getString("role","")!! == "Business Analyst"){
            binding.addNew.visibility=View.GONE
        }else{
            binding.addNew.visibility=View.VISIBLE
        }


        binding.tvDate.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", null)
        binding.addNew.setOnClickListener {
            onAddField()
        }
        binding.save.setOnClickListener {
            saveData()
        }
        setUpObserver()

    }

    private fun setUpObserver() {
        viewModel.beatPlanBPList.observe(requireActivity(), Event.EventObserver(
            onError = {
            },
            onLoading = {

            },
            onSuccess = { response ->
                if(response.data.isNotEmpty()){
                    setBpDropdownData(response.data)
                }
            }
        ))


        viewModel.listingGetTeamUser.observe(requireActivity(), Event.EventObserver(
            onError = {

            },
            onLoading = {

            },
            onSuccess = { response ->
                if(response.data.isNotEmpty()){
                    setEmployeeDropdownData(response.data)
                }
            }
        ))
    }

    private fun setBpDropdownData(data: List<DataBeatPlan>) {
        try {
            bpAdapter= ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, data.map { it.CardName })
            binding.firstBeatPlan.customerList.setAdapter(bpAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.firstBeatPlan.customerList.setOnItemClickListener { parent, view, position, id ->
            val selectedBP = parent.getItemAtPosition(position)
        }
        // Show the dropdown when the AutoCompleteTextView gains focus or is clicked
        binding.firstBeatPlan.customerList.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && binding.firstBeatPlan.customerList.text.isEmpty()) {
                        binding.firstBeatPlan.customerList.showDropDown()
                    }
                }

        // Also handle showing the dropdown when clicked (useful if the user clicks the field without typing)
        binding.firstBeatPlan.customerList.setOnClickListener {
            if (binding.firstBeatPlan.customerList.text.isEmpty()) {
                binding.firstBeatPlan.customerList.showDropDown()
            }
        }
    }

    private fun setEmployeeDropdownData(data: List<DataTeamList>) {
        try {
            salesEmployeeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, data)
            binding.firstBeatPlan.employeeList.setAdapter(salesEmployeeAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.firstBeatPlan.employeeList.setOnItemClickListener { parent, view, position, id ->
            val selectedUser = parent.getItemAtPosition(position) as DataTeamList
        }
        // Show the dropdown when the AutoCompleteTextView gains focus or is clicked
        binding.firstBeatPlan.employeeList.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.firstBeatPlan.employeeList.text.isEmpty()) {
                binding.firstBeatPlan.employeeList.showDropDown()
            }
        }

        // Also handle showing the dropdown when clicked (useful if the user clicks the field without typing)
        binding.firstBeatPlan.employeeList.setOnClickListener {
            if (binding.firstBeatPlan.employeeList.text.isEmpty()) {
                binding.firstBeatPlan.employeeList.showDropDown()
            }
        }
    }

    private fun onAddField() {
        val inflater = LayoutInflater.from(requireContext())
        val rowView: View = inflater.inflate(R.layout.item_add_beat_plan, null)
        val deletebutton = rowView.findViewById<ImageView>(R.id.delete)
        val customerlist = rowView.findViewById<AutoCompleteTextView>(R.id.customer_list)
        val employeelist = rowView.findViewById<AutoCompleteTextView>(R.id.employee_list)
        customerlist.setAdapter(bpAdapter)
        employeelist.setAdapter(salesEmployeeAdapter)
        deletebutton.visibility = View.VISIBLE
        deletebutton.setOnClickListener {
            onDelete(rowView)
        }
        employeelist.setOnItemClickListener { parent, view, position, id ->
            val selectedUser = parent.getItemAtPosition(position) as DataTeamList
        }
        customerlist.setOnItemClickListener { parent, view, position, id ->
            val selectedBP = parent.getItemAtPosition(position) as DataBeatPlan
        }
        customerlist.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && customerlist.text.isEmpty()) {
                customerlist.showDropDown()
            }
        }

        // Also handle showing the dropdown when clicked (useful if the user clicks the field without typing)
        customerlist.setOnClickListener {
            if (customerlist.text.isEmpty()) {
                customerlist.showDropDown()
            }
        }
        employeelist.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && employeelist.text.isEmpty()) {
                employeelist.showDropDown()
            }
        }

        // Also handle showing the dropdown when clicked (useful if the user clicks the field without typing)
        employeelist.setOnClickListener {
            if (employeelist.text.isEmpty()) {
                employeelist.showDropDown()
            }
        }
        binding.planContainer.addView(rowView, binding.planContainer.childCount - 1)
    }

    private fun onDelete(v: View) {
        binding.planContainer.removeView(v)
    }

    private fun saveData() {
        beatPlanList.clear()
        // this counts the no of child layout
        // inside the parent Linear layout
        val count = binding.planContainer.childCount
        var v: View?

      /*  for (i in 0 until count) {
            v = binding.planContainer.getChildAt(i)

            val languageName: EditText = v.findViewById(R.id.et_name)
            val experience: Spinner = v.findViewById(R.id.exp_spinner)

            // create an object of Language class
            val language = DataBeatPlan()
            language.name = languageName.text.toString()
            language.exp = experience.selectedItem as String

            // add the data to arraylist
            beatPlanList.add(language)
        }*/
    }
}