package com.antonio.gestortareas.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.antonio.gestortareas.model.Tarea

fun compartirTarea(context: Context, tarea: Tarea) {
    val descripcion = tarea.descripcion.ifBlank { "(sin descripción)" }
    val texto = "Tarea: ${tarea.titulo}\n" +
            "Descripción: $descripcion\n" +
            "Prioridad: ${tarea.prioridad.name}"

    val intentEnvio = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }

    try {
        context.startActivity(Intent.createChooser(intentEnvio, "Compartir tarea"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No hay ninguna aplicación compatible para compartir",
            Toast.LENGTH_SHORT
        ).show()
    }
}

