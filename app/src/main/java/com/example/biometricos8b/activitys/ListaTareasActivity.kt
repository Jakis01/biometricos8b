package com.example.biometricos8b.activitys

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometricos8b.config.RetrofitClient
import com.example.biometricos8b.data.Tarea
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasActivity(
    onNavigateToRegistrar: () -> Unit = {},
    onEditTarea: (Tarea) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var tareas by remember { mutableStateOf<List<Tarea>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    val cargarTareas = {
        scope.launch {
            cargando = true
            try {
                val response = RetrofitClient.apiService.obtenerTareas()
                if (response.isSuccessful) {
                    tareas = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Error al obtener tareas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarTareas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tareas", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { cargarTareas() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRegistrar,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (tareas.isEmpty()) {
                Text("No hay tareas registradas", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tareas, key = { it.id ?: (it.timestamp?.toString() ?: it.hashCode().toString()) }) { tarea ->
                        TareaCard(
                            tarea = tarea,
                            onEdit = { onEditTarea(tarea) },
                            onDelete = {
                                scope.launch {
                                    tarea.id?.let { id ->
                                        if (RetrofitClient.apiService.eliminarTarea(id).isSuccessful) {
                                            cargarTareas()
                                        }
                                    }
                                }
                            },
                            onComplete = {
                                scope.launch {
                                    tarea.id?.let { id ->
                                        val actualizada = tarea.copy(completada = !tarea.completada)
                                        if (RetrofitClient.apiService.actualizarTarea(id, actualizada).isSuccessful) {
                                            cargarTareas()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TareaCard(tarea: Tarea, onEdit: () -> Unit, onDelete: () -> Unit, onComplete: () -> Unit) {
    val prioridadColor = when (tarea.prioridad.lowercase()) {
        "alta" -> Color(0xFFE53935)
        "media" -> Color(0xFFFB8C00)
        "baja" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    val fecha = remember(tarea.timestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(tarea.timestamp ?: 0L))
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tarea.titulo, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.weight(1f),
                    color = if (tarea.completada) Color.Gray else Color.Unspecified
                )
                Surface(color = prioridadColor.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                    Text(text = tarea.prioridad, modifier = Modifier.padding(4.dp), color = prioridadColor, style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = tarea.descripcion, 
                maxLines = 2, 
                overflow = TextOverflow.Ellipsis,
                color = if (tarea.completada) Color.Gray else Color.Unspecified
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = if (tarea.completada) "✅ Completada" else "⏳ Pendiente", style = MaterialTheme.typography.labelMedium)
                    Text(text = fecha, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // El icono de completado ahora está al lado del lapicito
                    IconButton(onClick = onComplete) {
                        Icon(
                            imageVector = if (tarea.completada) Icons.Default.CheckCircle else Icons.Default.RadioButtonChecked,
                            contentDescription = "Estado",
                            tint = if (tarea.completada) Color(0xFF43A047) else Color.Gray
                        )
                    }
                    IconButton(onClick = onEdit) { 
                        Icon(Icons.Default.Edit, contentDescription = "Editar") 
                    }
                    IconButton(onClick = onDelete) { 
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red) 
                    }
                }
            }
        }
    }
}
