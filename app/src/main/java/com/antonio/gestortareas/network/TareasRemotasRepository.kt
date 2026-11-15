package com.antonio.gestortareas.network

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

sealed interface EstadoTareasRemotas {
    data object Inicial : EstadoTareasRemotas
    data object Cargando : EstadoTareasRemotas
    data class Exito(val tareas: List<TareaRemota>) : EstadoTareasRemotas
    data class Fallo(val mensaje: String) : EstadoTareasRemotas
}

class TareasRemotasRepository(private val api: JsonPlaceholderApi) {

    suspend fun obtenerTareasDeEjemplo(): EstadoTareasRemotas {
        return try {
            EstadoTareasRemotas.Exito(api.obtenerTareas(userId = 1))
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            EstadoTareasRemotas.Fallo(
                "No se pudo conectar con el servidor. Comprueba tu conexión a Internet."
            )
        } catch (e: HttpException) {
            EstadoTareasRemotas.Fallo(
                "El servidor respondió con un error (código ${e.code()})."
            )
        } catch (e: Exception) {
            EstadoTareasRemotas.Fallo("Ocurrió un error inesperado al leer los datos.")
        }
    }
}

