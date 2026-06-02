package com.example.biometricos8b.activitys

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.biometricos8b.config.RetrofitClient
import com.example.biometricos8b.data.Tarea
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasActivity() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var tareas by remember { mutableStateOf<List<Tarea>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerTareas()

                if (response.isSuccessful) {
                    tareas = response.body() ?: emptyList()
                } else {
                    Toast.makeText(
                        context,
                        "Error al cargar estadísticas",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error de red",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                cargando = false
            }
        }
    }

    val total = tareas.size
    val completadas = tareas.count { it.completada }
    val pendientes = total - completadas

    val chartData = listOf(
        Bars(
            label = "Estado",
            values = listOf(
                Bars.Data(
                    label = "Completadas",
                    value = completadas.toDouble(),
                    color = Brush.verticalGradient(
                        listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF81C784)
                        )
                    )
                ),
                Bars.Data(
                    label = "Pendientes",
                    value = pendientes.toDouble(),
                    color = Brush.verticalGradient(
                        listOf(
                            Color(0xFFF44336),
                            Color(0xFFE57373)
                        )
                    )
                )
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Estadísticas")
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (cargando) {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Total de tareas",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = total.toString(),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Completadas",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = completadas.toString(),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Pendientes",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = pendientes.toString(),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }

                    Text(
                        text = "Gráfica de tareas",
                        fontWeight = FontWeight.Bold
                    )

                    ColumnChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        data = chartData
                    )
                }
            }
        }
    }
}