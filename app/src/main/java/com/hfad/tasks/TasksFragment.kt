package com.hfad.tasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        val application = requireNotNull(this.activity).application
        val dao = TaskDB.getInstance(application).taskDao
        val viewModel =
            ViewModelProvider(this, TaskViewModelFactory(dao))[TaskViewModel::class.java]
        _binding = FragmentTasksBinding.inflate(inflater, container, false).apply {
            composeView.setContent {
                view?.let {
                    TaskFragmentContent(view = it, viewModel = viewModel)
                }
            }
        }
        binding.lifecycleOwner = viewLifecycleOwner

        val view = binding.root
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
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun TaskFragmentContent(view: View, viewModel: TaskViewModel) {
    var taskName = remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        EnterTaskName(taskName = taskName.value) {
            taskName.value = it
        }
        SaveTaskButton {
            viewModel.newTaskName = taskName.value
            viewModel.insertTask()
            taskName.value = ""
        }
    }
}

@Composable
fun EnterTaskName(taskName: String, changed: (String) -> Unit) {
    TextField(
        value = taskName,
        onValueChange = changed,
        label = { Text(text = "Enter a task name") })
}

@Composable
fun SaveTaskButton(clicked: () -> Unit) {
    Button(onClick = clicked) {
        Text(text = "Save task")
    }
}