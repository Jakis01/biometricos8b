package com.example.biometricos8b.activitys

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.biometricos8b.R
import com.example.biometricos8b.componentes.DashboardCard

@Composable
fun HomeActivity(
    onNavigateToRegistrarTarea: () -> Unit,
    onNavigateToListaTareas: () -> Unit,
    onNavigateToRegistrarNota: () -> Unit,
    onNavigateToListaNotas: () -> Unit,
    onNavigateToEstadisticas: () -> Unit
) {
    val primaryColor = Color(0xFF002868)
    val secondaryColor = Color(0xFF8B80F8)
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(primaryColor, Color(0xFF1A4B8F))
    )

    Scaffold(
        containerColor = Color(0xFFF8F9FF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- CABECERA ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = gradientBrush,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(45.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("¡Hola!", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge)
                            Text("Jaki", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Image(
                        painter = painterResource(id = R.drawable.logoapp),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            // --- DASHBOARD ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Acciones rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DashboardCard(
                            title = "Nueva Tarea",
                            icon = Icons.Default.AddCircle,
                            containerColor = Color(0xFFE8EAF6),
                            contentColor = primaryColor,
                            onClick = onNavigateToRegistrarTarea
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Ver Tareas",
                            icon = Icons.AutoMirrored.Filled.List,
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF2E7D32),
                            onClick = onNavigateToListaTareas
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Nueva Nota",
                            icon = Icons.Default.Description,
                            containerColor = Color(0xFFFFF3E0),
                            contentColor = Color(0xFFE65100),
                            onClick = onNavigateToRegistrarNota
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Mis Notas",
                            icon = Icons.AutoMirrored.Filled.Notes,
                            containerColor = Color(0xFFF3E5F5),
                            contentColor = Color(0xFF6A1B9A),
                            onClick = onNavigateToListaNotas
                        )
                    }
                    item {
                        DashboardCard(
                            title = "Estadísticas",
                            icon = Icons.Default.BarChart,
                            containerColor = Color(0xFFE3F2FD),
                            contentColor = Color(0xFF1565C0),
                            onClick = onNavigateToEstadisticas

                        )
                    }
                    }
                }
            }
        }
    }

