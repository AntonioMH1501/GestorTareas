package com.antonio.gestortareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antonio.gestortareas.model.Prioridad
import com.antonio.gestortareas.model.Tarea

fun validarTitulo(titulo: String): String? {
    val tituloLimpio = titulo.trim()
    return when {
        tituloLimpio.isEmpty() -> "El título no puede estar vacío"
        tituloLimpio.length < 3 -> "El título debe tener al menos 3 caracteres"
        else -> null
    }
}

@Composable
fun FormularioTareaScreen(
    tareaInicial: Tarea?,
    onGuardar: (String, String, Prioridad) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable { mutableStateOf(tareaInicial?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tareaInicial?.descripcion ?: "") }
    var prioridad by rememberSaveable { mutableStateOf(tareaInicial?.prioridad ?: Prioridad.MEDIA) }
    var errorTitulo by rememberSaveable { mutableStateOf<String?>(null) }
    var guardando by rememberSaveable { mutableStateOf(false) }
    var menuAbierto by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (tareaInicial == null) "Nueva tarea" else "Editar tarea",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
                errorTitulo = null
            },
            label = { Text("Título") },
            isError = errorTitulo != null,
            supportingText = { errorTitulo?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción (opcional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            OutlinedButton(onClick = { menuAbierto = true }) {
                Text("Prioridad: ${prioridad.name}")
            }
            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                Prioridad.entries.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.name) },
                        onClick = {
                            prioridad = opcion
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }
            Button(
                enabled = !guardando,
                onClick = {
                    val error = validarTitulo(titulo)
                    if (error != null) {
                        errorTitulo = error
                    } else {
                        guardando = true
                        onGuardar(titulo.trim(), descripcion.trim(), prioridad)
                    }
                }
            ) {
                Text("Guardar")
            }
        }
    }
}