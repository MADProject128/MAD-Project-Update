package com.example.todolistwithpriortization

import android.os.Bundle
import androidx.compose.material3.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolistwithpriortization.ui.theme.ToDoListWithPriortizationTheme
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListWithPriortizationTheme {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Column {
                        AppBar()
                        SwipeToDeleteList()
                    }
                    FloatingActionButton(
                        onClick = { /* Show dialog or navigate to add task screen */ },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                        containerColor = Color.Blue
                    ) {
                        Text("+", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun AppBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("To-Do List", color = Color.White)
    }
}

@Composable
fun SwipeToDeleteList(modifier: Modifier = Modifier) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var newTaskTitle by remember { mutableStateOf(TextFieldValue("")) }
    var newTaskDescription by remember { mutableStateOf(TextFieldValue("")) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }

    Column(modifier = modifier.fillMaxSize().padding(top = 16.dp)) {
        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newTaskTitle = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Task Description") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Priority: ")
            Priority.values().forEach { priority ->
                RadioButton(
                    selected = selectedPriority == priority,
                    onClick = { selectedPriority = priority }
                )
                Text(priority.name)
            }
        }
        Button(
            onClick = {
                if (newTaskTitle.text.isNotEmpty()) {
                    tasks = tasks + Task(newTaskTitle.text, newTaskDescription.text, selectedPriority)
                    newTaskTitle = TextFieldValue("")
                    newTaskDescription = TextFieldValue("")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Add Task")
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(tasks.size) { index ->
                val task = tasks[index]
                SwipeTaskItem(task, onDelete = { tasks = tasks.toMutableList().also { it.removeAt(index) } })
            }
        }
    }
}

@Composable
fun SwipeTaskItem(task: Task, onDelete: () -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                offsetX += dragAmount
                if (abs(offsetX) > 300) {
                    onDelete()
                }
            }
        }
    ) {
        CustomCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            color = when (task.priority) {
                Priority.HIGH -> Color.Red.copy(alpha = 0.2f)
                Priority.MEDIUM -> Color.Yellow.copy(alpha = 0.2f)
                Priority.LOW -> Color.Green.copy(alpha = 0.2f)
            }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(task.title)
                Spacer(modifier = Modifier.weight(1f))
                Text(task.description)
            }
        }
    }
}

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SwipeToDeleteListPreview() {
    ToDoListWithPriortizationTheme {
        SwipeToDeleteList()
    }
}

data class Task(val title: String, val description: String, val priority: Priority)

enum class Priority {
    HIGH, MEDIUM, LOW
}
