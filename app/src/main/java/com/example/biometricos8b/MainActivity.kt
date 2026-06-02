package com.example.biometricos8b

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.biometricos8b.activitys.HomeActivity
import com.example.biometricos8b.activitys.ListaNotasActivity
import com.example.biometricos8b.activitys.ListaTareasActivity
import com.example.biometricos8b.activitys.LoginActivity
import com.example.biometricos8b.activitys.RegistrarNotaActivity
import com.example.biometricos8b.activitys.RegistrarTareaActivity
import com.example.biometricos8b.ui.theme.Biometricos8bTheme
import com.example.biometricos8b.activitys.EstadisticasActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Biometricos8bTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginActivity(onAutenticacionExitosa = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            })
                        }
                        composable("home") {
                            HomeActivity(
                                onNavigateToRegistrarTarea = { navController.navigate("registrar_tarea") },
                                onNavigateToListaTareas = { navController.navigate("lista_tareas") },
                                onNavigateToRegistrarNota = { navController.navigate("registrar_nota") },
                                onNavigateToListaNotas = { navController.navigate("lista_notas") },
                                onNavigateToEstadisticas = {
                                    navController.navigate("estadisticas")
                                })
                        }
                        composable(
                            route = "registrar_tarea?id={id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            RegistrarTareaActivity(
                                tareaId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("lista_tareas") {
                            ListaTareasActivity(
                                onNavigateToRegistrar = { navController.navigate("registrar_tarea") },
                                onEditTarea = { tarea ->
                                    navController.navigate("registrar_tarea?id=${tarea.id}")
                                }
                            )
                        }
                        composable(
                            route = "registrar_nota?id={id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            RegistrarNotaActivity(
                                notaId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("lista_notas") {
                            ListaNotasActivity(
                                onNavigateToRegistrar = { navController.navigate("registrar_nota") },
                                onEditNota = { nota ->
                                    navController.navigate("registrar_nota?id=${nota.id}")
                                }
                            )
                        }
                        composable("estadisticas") {
                            EstadisticasActivity()
                        }
                    }
                }
                        }
                    }
                }
            }



