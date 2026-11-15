package com.antonio.gestortareas.data

import com.antonio.gestortareas.model.Tarea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TareaRepository(private val tareaDao: TareaDao) {

    val tareas: Flow<List<Tarea>> = tareaDao.observarTodas()
        .map { entidades -> entidades.map { it.toTarea() } }

    suspend fun insertar(tarea: Tarea) = tareaDao.insertar(tarea.toEntity())

    suspend fun actualizar(tarea: Tarea) = tareaDao.actualizar(tarea.toEntity())

    suspend fun eliminar(tarea: Tarea) = tareaDao.eliminar(tarea.toEntity())
}

