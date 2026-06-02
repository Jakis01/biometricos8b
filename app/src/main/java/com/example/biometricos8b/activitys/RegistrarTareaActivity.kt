package com.example.biometricos8b.activitys

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometricos8b.config.RetrofitClient
import com.example.biometricos8b.data.Tarea
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarTareaActivity(tareaId: String? = null, onNavigateBack: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val brandBlue = Color(0xFF002868)
    val brandPurple = Color(0xFF8B80F8)


    val opcionesCategoria = listOf("Trabajo", "Estudio", "Hogar", "Personal")
    val opcionesPrioridad = listOf("Alta", "Media", "Baja")


    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    var isListening by remember { mutableStateOf(false) }

    val recognitionListener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) { 
                isListening = false
                Toast.makeText(context, "Dictado no disponible o sin permiso de audio", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    descripcion = matches[0]
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(recognitionListener)
        onDispose { speechRecognizer.destroy() }
    }


    var categoria by remember { mutableStateOf("Trabajo") }
    var prioridad by remember { mutableStateOf("Media") }
    var completada by remember { mutableStateOf(false) }
    var expandirCategoria by remember { mutableStateOf(false) }
    var expandirPrioridad by remember { mutableStateOf(false) }

    val esModoEdicion = tareaId != null
    val esFormularioValido = titulo.isNotBlank() && descripcion.isNotBlank()

    LaunchedEffect(tareaId) {
        if (esModoEdicion && tareaId != null) {
            try {
                val response = RetrofitClient.apiService.obtenerTareaPorId(tareaId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        titulo = it.titulo
                        descripcion = it.descripcion
                        categoria = it.categoria
                        prioridad = it.prioridad
                        completada = it.completada
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (esModoEdicion) "Editar Actividad" else "Nueva Tarea", fontWeight = FontWeight.ExtraBold, color = brandBlue) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = brandBlue)
                    }
                }
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = Color.White) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val tarea = Tarea(
                                    id = if (esModoEdicion) tareaId else null,
                                    titulo = titulo, descripcion = descripcion,
                                    categoria = categoria, prioridad = prioridad, completada = completada
                                )
                                val response = if (esModoEdicion && tareaId != null) 
                                    RetrofitClient.apiService.actualizarTarea(tareaId, tarea)
                                else 
                                    RetrofitClient.apiService.crearTarea(tarea)
                                
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "¡Guardado!", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                } else {
                                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    enabled = esFormularioValido,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                ) {
                    Text(if (esModoEdicion) "Confirmar" else "Guardar", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF8F9FF)).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = titulo, onValueChange = { titulo = it },
                        label = { Text("Título") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = descripcion, onValueChange = { descripcion = it },
                        label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (!isListening) {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
                                    }
                                    try {
                                        speechRecognizer.startListening(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Micrófono no disponible", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    speechRecognizer.stopListening()
                                }
                            }) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isListening) Color.Red else brandPurple
                                )
                            }
                        }
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(expanded = expandirCategoria, onExpandedChange = { expandirCategoria = !expandirCategoria }, modifier = Modifier.weight(1f)) {
                            OutlinedTextField(value = categoria, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirCategoria) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                            ExposedDropdownMenu(expanded = expandirCategoria, onDismissRequest = { expandirCategoria = false }) {
                                opcionesCategoria.forEach { label ->
                                    DropdownMenuItem(text = { Text(label) }, onClick = { categoria = label; expandirCategoria = false })
                                }
                            }
                        }
                        ExposedDropdownMenuBox(expanded = expandirPrioridad, onExpandedChange = { expandirPrioridad = !expandirPrioridad }, modifier = Modifier.weight(1f)) {
                            OutlinedTextField(value = prioridad, onValueChange = {}, readOnly = true, label = { Text("Prioridad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirPrioridad) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                            ExposedDropdownMenu(expanded = expandirPrioridad, onDismissRequest = { expandirPrioridad = false }) {
                                opcionesPrioridad.forEach { label ->
                                    DropdownMenuItem(text = { Text(label) }, onClick = { prioridad = label; expandirPrioridad = false })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
