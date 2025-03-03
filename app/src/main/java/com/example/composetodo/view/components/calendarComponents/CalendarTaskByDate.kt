package com.example.composetodo.view.components.calendarComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.composetodo.model.Task
import com.example.composetodo.presenter.TaskPresenter
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Contenedor para mostrar las tareas de una fecha específica en el contexto del calendario.
 * Este componente encapsula toda la lógica de visualización de tareas para una fecha seleccionada
 * en la pantalla de calendario.
 */
@Composable
fun TaskByDateCalendarComponent(
    selectedDate: LocalDate,
    tasksForDate: List<Task>,
    today: LocalDate,
    viewModel: TaskPresenter,
    onNavigateToAddTask: (LocalDate) -> Unit,
    onNavigateToEditTask: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título de la sección
            Text(
                text = "Tareas para el ${viewModel.formatDate(selectedDate)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Botón para añadir tarea (solo visible para fechas futuras o actuales)
            if (!selectedDate.isBefore(today)) {
                AddTaskButton(
                    onClick = { onNavigateToAddTask(selectedDate) }
                )

                if (tasksForDate.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Mostrar mensaje si no hay tareas o la lista de tareas
            if (tasksForDate.isEmpty()) {
                EmptyTasksMessage(
                    selectedDate,
                    today
                )
            } else {
                TasksList(
                    tasksForDate = tasksForDate,
                    onTaskCheckedChange = { taskId, isCompleted ->
                        viewModel.updateTaskStatus(taskId, isCompleted)
                    },
                    onDeleteTask = { task ->
                        scope.launch {
                            // Guardar la tarea localmente antes de eliminarla
                            val deletedTask = task
                            
                            // Eliminar la tarea
                            viewModel.deleteTask(task.id)
                            
                            // Mostrar el Snackbar
                            val result = snackbarHostState.showSnackbar(
                                message = "Has eliminado una tarea, si te has equivocado dale recuperar",
                                actionLabel = "Recuperar",
                                duration = SnackbarDuration.Short
                            )
                            
                            // Si se hace clic en "Recuperar", recuperar la tarea
                            if (result == SnackbarResult.ActionPerformed) {
                                // Recuperar la tarea usando la variable local
                                viewModel.undoDeleteTask(deletedTask)
                            }
                        }
                    },
                    onEditTask = onNavigateToEditTask
                )
            }
        }
    }
} 