package com.example.biometricos8b.activitys

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.biometricos8b.R


fun Context.getActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
fun LoginActivity(onAutenticacionExitosa: () -> Unit) {
    val context = LocalContext.current
    val activity = context.getActivity()
    val isDarkTheme = isSystemInDarkTheme()

    val sloganColor = if (isDarkTheme) Color(0xFFB39DDB) else Color(0xFF8B80F8)
    val buttonColor = if (isDarkTheme) Color(0xFFD0BCFF) else Color(0xFF002868)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = null,
                    modifier = Modifier.size(260.dp)
                )
                Text(
                    text = "Tu bitácora personal",
                    style = MaterialTheme.typography.titleMedium,
                    color = sloganColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (activity != null) {
                        lanzarBiometricos(activity, onAutenticacionExitosa)
                    } else {
                        Toast.makeText(context, "Error interno: No se detectó la actividad", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = if (isDarkTheme) Color.Black else Color.White
                )
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Entrar con huella", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun lanzarBiometricos(activity: FragmentActivity, onAutenticacionExitosa: () -> Unit) {
    val ejecutor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, ejecutor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(activity, "Error: $errString", Toast.LENGTH_SHORT).show()
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(activity, "Huella no reconocida", Toast.LENGTH_SHORT).show()
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(activity, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                onAutenticacionExitosa()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Acceso Seguro")
        .setSubtitle("Confirma tu identidad")
        .setNegativeButtonText("Cancelar")
        .build()

    biometricPrompt.authenticate(promptInfo)
}
