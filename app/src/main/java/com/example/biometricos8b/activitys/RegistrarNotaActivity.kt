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
import com.example.biometricos8b.data.Nota
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarNotaActivity(notaId: String? = null, onNavigateBack: () -> Unit) {

    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val brandBlue = Color(0xFF002868)
    val brandPurple = Color(0xFF8B80F8)

    // Configuración del Reconocedor de Voz Directo
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
                Toast.makeText(context, "Micrófono no disponible", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val nuevoTexto = matches[0]
                    // Añadimos el texto al final para no borrar lo anterior
                    contenido = if (contenido.isBlank()) nuevoTexto else "$contenido $nuevoTexto"
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

    val esFormularioValido = titulo.isNotBlank() && contenido.isNotBlank()
    val esModoEdicion = notaId != null

    LaunchedEffect(notaId) {
        if (esModoEdicion && notaId != null) {
            try {
                val response = RetrofitClient.apiService.obtenerNotaPorId(notaId)
                if (response.isSuccessful) {
                    response.body()?.let { nota ->
                        titulo = nota.titulo
                        contenido = nota.contenido
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar la nota", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (esModoEdicion) "Editar Nota" else "Nueva Nota",
                        fontWeight = FontWeight.ExtraBold,
                        color = brandBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = brandBlue)
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
                                val nota = Nota(id = notaId, titulo = titulo, contenido = contenido)
                                val response = if (esModoEdicion && notaId != null) 
                                    RetrofitClient.apiService.actualizarNota(notaId, nota)
                                else 
                                    RetrofitClient.apiService.crearNota(nota)

                                if (response.isSuccessful) {
                                    Toast.makeText(context, "¡Guardado!", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
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
                    Text(if (esModoEdicion) "Actualizar Nota" else "Guardar Nota", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FF))
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Tu nota", style = MaterialTheme.typography.titleSmall, color = brandPurple, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = titulo, onValueChange = { titulo = it },
                        label = { Text("Título") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = brandPurple) }
                    )
                    OutlinedTextField(
                        value = contenido, onValueChange = { contenido = it },
                        label = { Text("Cuerpo de la nota") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 250.dp),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = brandPurple) },
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
                                        Toast.makeText(context, "Error al activar voz", Toast.LENGTH_SHORT).show()
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
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
