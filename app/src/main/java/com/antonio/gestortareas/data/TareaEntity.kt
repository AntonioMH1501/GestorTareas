package com.antonio.gestortareas.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.antonio.gestortareas.model.Prioridad
import com.antonio.gestortareas.model.Tarea

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val prioridad: String,
    val completada: Boolean
)

fun TareaEntity.toTarea(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    prioridad = Prioridad.valueOf(prioridad),
    completada = completada
)

fun Tarea.toEntity(): TareaEntity = TareaEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    prioridad = prioridad.name,
    completada = completada
)

