package com.antonio.gestortareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antonio.gestortareas.network.EstadoTareasRemotas
import com.antonio.gestortareas.network.TareaRemota

@Composable
fun TareasEjemploScreen(
    estado: EstadoTareasRemotas,
    onCargar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tareas de ejemplo",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Estas tareas vienen de un servidor externo (JSONPlaceholder). " +
                    "No forman parte de tu lista personal ni se guardan en el móvil."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onVolver) {
                Text("Volver")
            }
            Button(
                onClick = onCargar,
                enabled = estado !is EstadoTareasRemotas.Cargando
            ) {
                Text("Cargar tareas")
            }
        }

        when (estado) {
            EstadoTareasRemotas.Inicial -> {
                Text("Pulsa «Cargar tareas» para consultar el servidor.")
            }

            EstadoTareasRemotas.Cargando -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is EstadoTareasRemotas.Exito -> {
                if (estado.tareas.isEmpty()) {
                    Text("El servidor no ha devuelto ninguna tarea.")
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(estado.tareas, key = { tarea -> tarea.id }) { tarea ->
                            TareaRemotaItem(tarea = tarea)
                        }
                    }
                }
            }

            is EstadoTareasRemotas.Fallo -> {
                Text(
                    text = estado.mensaje,
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = onCargar) {
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
fun TareaRemotaItem(
    tarea: TareaRemota,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ID: ${tarea.id}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = tarea.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = if (tarea.completed) "Completada" else "Pendiente")
        }
    }
}

