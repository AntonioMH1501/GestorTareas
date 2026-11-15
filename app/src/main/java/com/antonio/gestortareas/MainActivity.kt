package com.antonio.gestortareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.antonio.gestortareas.data.AppDatabase
import com.antonio.gestortareas.data.PreferenciasRepository
import com.antonio.gestortareas.data.TareaRepository
import com.antonio.gestortareas.model.Tarea
import com.antonio.gestortareas.network.ClienteRetrofit
import com.antonio.gestortareas.network.EstadoTareasRemotas
import com.antonio.gestortareas.network.TareasRemotasRepository
import com.antonio.gestortareas.ui.FormularioTareaScreen
import com.antonio.gestortareas.ui.ListaTareasScreen
import com.antonio.gestortareas.ui.TareasEjemploScreen
import com.antonio.gestortareas.ui.compartirTarea
import com.antonio.gestortareas.ui.theme.GestorTareasTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repositorio = TareaRepository(
            AppDatabase.obtener(applicationContext).tareaDao()
        )
        val preferencias = PreferenciasRepository(applicationContext)
        val repositorioRemoto = TareasRemotasRepository(ClienteRetrofit.api)

        setContent {
            GestorTareasTheme {
                val tareas by repositorio.tareas.collectAsState(initial = emptyList())
                val soloPendientes by preferencias.soloPendientes.collectAsState(initial = false)
                val tareasVisibles = if (soloPendientes) {
                    tareas.filter { !it.completada }
                } else {
                    tareas
                }

                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                var pantalla by rememberSaveable { mutableStateOf(Pantalla.LISTA) }
                var idEnEdicion by rememberSaveable { mutableStateOf<Int?>(null) }
                val tareaEnEdicion = tareas.find { it.id == idEnEdicion }
                var estadoApi by remember {
                    mutableStateOf<EstadoTareasRemotas>(EstadoTareasRemotas.Inicial)
                }

                BackHandler(enabled = pantalla != Pantalla.LISTA) {
                    idEnEdicion = null
                    pantalla = Pantalla.LISTA
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (pantalla == Pantalla.LISTA) {
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ExtendedFloatingActionButton(
                                    onClick = { pantalla = Pantalla.EJEMPLO }
                                ) {
                                    Text("Tareas de ejemplo")
                                }
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        idEnEdicion = null
                                        pantalla = Pantalla.FORMULARIO
                                    }
                                ) {
                                    Text("Nueva tarea")
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val modifierPantalla = Modifier
                        .padding(innerPadding)
                        .widthIn(max = 640.dp)

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        when (pantalla) {
                            Pantalla.LISTA -> ListaTareasScreen(
                                tareas = tareasVisibles,
                                soloPendientes = soloPendientes,
                                onSoloPendientesChange = { nuevoValor ->
                                    scope.launch { preferencias.guardarSoloPendientes(nuevoValor) }
                                },
                                onCompletadaChange = { tarea, completada ->
                                    scope.launch {
                                        repositorio.actualizar(tarea.copy(completada = completada))
                                    }
                                },
                                onEditar = { tarea ->
                                    idEnEdicion = tarea.id
                                    pantalla = Pantalla.FORMULARIO
                                },
                                onCompartir = { tarea -> compartirTarea(context, tarea) },
                                onEliminar = { tarea ->
                                    scope.launch { repositorio.eliminar(tarea) }
                                },
                                modifier = modifierPantalla
                            )

                            Pantalla.FORMULARIO -> FormularioTareaScreen(
                                tareaInicial = tareaEnEdicion,
                                onGuardar = { titulo, descripcion, prioridad ->
                                    scope.launch {
                                        if (tareaEnEdicion != null) {
                                            repositorio.actualizar(
                                                tareaEnEdicion.copy(
                                                    titulo = titulo,
                                                    descripcion = descripcion,
                                                    prioridad = prioridad
                                                )
                                            )
                                        } else {
                                            repositorio.insertar(
                                                Tarea(0, titulo, descripcion, prioridad, false)
                                            )
                                        }
                                        idEnEdicion = null
                                        pantalla = Pantalla.LISTA
                                    }
                                },
                                onCancelar = {
                                    idEnEdicion = null
                                    pantalla = Pantalla.LISTA
                                },
                                modifier = modifierPantalla
                            )

                            Pantalla.EJEMPLO -> TareasEjemploScreen(
                                estado = estadoApi,
                                onCargar = {
                                    if (estadoApi !is EstadoTareasRemotas.Cargando) {
                                        estadoApi = EstadoTareasRemotas.Cargando
                                        scope.launch {
                                            estadoApi = repositorioRemoto.obtenerTareasDeEjemplo()
                                        }
                                    }
                                },
                                onVolver = { pantalla = Pantalla.LISTA },
                                modifier = modifierPantalla
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Pantalla {
    LISTA,
    FORMULARIO,
    EJEMPLO
}