package com.antonio.gestortareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.antonio.gestortareas.R
import com.antonio.gestortareas.model.Tarea

@Composable
fun ListaTareasScreen(
    tareas: List<Tarea>,
    soloPendientes: Boolean,
    onSoloPendientesChange: (Boolean) -> Unit,
    onCompletadaChange: (Tarea, Boolean) -> Unit,
    onEditar: (Tarea) -> Unit,
    onCompartir: (Tarea) -> Unit,
    onEliminar: (Tarea) -> Unit,
    modifier: Modifier = Modifier
) {
    var tareaAEliminar by remember { mutableStateOf<Tarea?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Mostrar solo pendientes")
            Switch(
                checked = soloPendientes,
                onCheckedChange = onSoloPendientesChange
            )
        }

        if (tareas.isEmpty()) {
            val mensajeVacio = if (soloPendientes) {
                "No tienes tareas pendientes."
            } else {
                "No tienes tareas todavía. Cuando añadas una, aparecerá aquí."
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mensajeVacio)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareas, key = { tarea -> tarea.id }) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onCompletadaChange = { completada -> onCompletadaChange(tarea, completada) },
                        onEditarClick = { onEditar(tarea) },
                        onCompartirClick = { onCompartir(tarea) },
                        onEliminarClick = { tareaAEliminar = tarea }
                    )
                }
            }
        }
    }

    tareaAEliminar?.let { tarea ->
        AlertDialog(
            onDismissRequest = { tareaAEliminar = null },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Seguro que quieres eliminar \"${tarea.titulo}\"? No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar(tarea)
                    tareaAEliminar = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { tareaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TareaItem(
    tarea: Tarea,
    onCompletadaChange: (Boolean) -> Unit,
    onEditarClick: () -> Unit,
    onCompartirClick: () -> Unit,
    onEliminarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onCompletadaChange
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Prioridad: ${tarea.prioridad.name}")
                Text(text = if (tarea.completada) "Completada" else "Pendiente")
            }
            IconButton(onClick = onEditarClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_editar),
                    contentDescription = "Editar tarea"
                )
            }
            IconButton(onClick = onCompartirClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_compartir),
                    contentDescription = "Compartir tarea"
                )
            }
            IconButton(onClick = onEliminarClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eliminar),
                    contentDescription = "Eliminar tarea"
                )
            }
        }
    }
}