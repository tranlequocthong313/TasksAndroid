package com.hfad.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hfad.tasks.databinding.FragmentTasksBinding

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding: FragmentTasksBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val view = binding.root
        val application = requireNotNull(this.activity).application
        val dao = TaskDB.getInstance(application).taskDao
        val viewModel =
            ViewModelProvider(this, TaskViewModelFactory(dao))[TaskViewModel::class.java]
        binding.taskViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateToTask.observe(viewLifecycleOwner) { newVal ->
            newVal?.let {
                val action = TasksFragmentDirections.actionTasksFragmentToEditTaskFragment(newVal)
                this.findNavController().navigate(action)
                viewModel.onTaskNavigated()
            }
        }
        val adapter = TaskItemAdapter { id ->
            Toast.makeText(binding.root.context, "Clicked task $id", Toast.LENGTH_SHORT).show()
            viewModel.onTaskClicked(id)
        }
        binding.taskList.adapter = adapter
        viewModel.tasks.observe(viewLifecycleOwner) { newVal ->
            adapter.submitList(newVal)
            binding.taskName.text = null
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}