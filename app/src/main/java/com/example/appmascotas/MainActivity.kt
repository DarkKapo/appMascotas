package com.example.appmascotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmascotas.datos.BaseDeDatosPaseos
import com.example.appmascotas.datos.EntidadPaseoMascota
import com.example.appmascotas.modelovista.ModeloVistaPaseos
import com.example.appmascotas.repositorio.RepositorioPaseosMascotas
import com.example.appmascotas.ui.theme.AppMascotasTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMascotasTheme {
                AppPaseosMascotas()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPaseosMascotas(){
    //Config BD y viewmodel
    val context = LocalContext.current
    val db = BaseDeDatosPaseos.obtenerBD(context)
    val repositorio = RepositorioPaseosMascotas(db.AccesoDatosPaseos())
    val viewModel: ModeloVistaPaseos = viewModel { ModeloVistaPaseos(repositorio) }

    var mostrarOcultarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Control de Paseos",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "🐾 Bienvenido/a",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarOcultarFormulario = !mostrarOcultarFormulario }
            ) {
                Icon(
                    imageVector = if (mostrarOcultarFormulario) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (mostrarOcultarFormulario) "Cerrar" else "Agregar paseo"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Muestra estadísticas
            TarjetaEstadisticas(viewModel)

            Spacer(modifier = Modifier.height(16.dp))
            //Muestra formulario o lista
            if (mostrarOcultarFormulario) {
                FormularioNuevoPaseo(viewModel) {
                    mostrarOcultarFormulario = false
                }
            } else {
                ListaDePaseos(viewModel)
            }
        }
    }
}
// Tarjeta que muestra las estadísticas de dinero
@Composable
fun TarjetaEstadisticas(viewModel: ModeloVistaPaseos) {
    val totalGanado by viewModel.totalGanado.collectAsState()
    val totalPendiente by viewModel.totalPendiente.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "\uD83D\uDCCC Estadísticas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\uD83D\uDCB8 Ganado",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatearDinero(totalGanado),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF4CAF50), // Verde
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\uD83D\uDD50 Pendiente",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatearDinero(totalPendiente),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFFF9800), // Naranja
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\uD83D\uDCB3 Total",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatearDinero(totalGanado + totalPendiente),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Formulario nuevo paseo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioNuevoPaseo(
    viewModel: ModeloVistaPaseos,
    onPaseoAgregado: () -> Unit
) {
    // Obtener estados de formulario de viewModel
    val nombreMascota by viewModel.nombreMascota.collectAsState()
    val tipoMascota by viewModel.tipoMascota.collectAsState()
    val nombreCliente by viewModel.nombreCliente.collectAsState()
    val duracionPaseo by viewModel.duracionPaseo.collectAsState()
    val precioPorHora by viewModel.precioPorHora.collectAsState()
    val comentario by viewModel.comentario.collectAsState()

    // Select tipo de mascotas
    var expandedTipoMascota by remember { mutableStateOf(false) }
    val tiposMascotas = listOf("Perro", "Gato", "Conejo", "Otro")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Agrega Scroll
        ) {
            Text(
                text = "➕ Nuevo Paseo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Campos del formulario
            OutlinedTextField(
                value = nombreMascota,
                onValueChange = viewModel::actualizarNombreMascota,
                label = { Text("\uD83D\uDC36 Nombre de la mascota") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedTipoMascota,
                onExpandedChange = { expandedTipoMascota = !expandedTipoMascota }
            ) {
                OutlinedTextField(
                    value = tipoMascota,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("🐾 Tipo de mascota") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipoMascota)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedTipoMascota,
                    onDismissRequest = { expandedTipoMascota = false }
                ) {
                    tiposMascotas.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                viewModel.actualizarTipoMascota(tipo)
                                expandedTipoMascota = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombreCliente,
                onValueChange = viewModel::actualizarNombreCliente,
                label = { Text("👤 Nombre del cliente") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = duracionPaseo,
                    onValueChange = viewModel::actualizarDuracionPaseo,
                    label = { Text("\uD83D\uDD53 Horas") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = precioPorHora,
                    onValueChange = viewModel::actualizarPrecioPorHora,
                    label = { Text("\uD83D\uDCB2 Precio/hora") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calcula el total
            if (duracionPaseo.isNotEmpty() && precioPorHora.isNotEmpty()) {
                val total = viewModel.calcularTotalGanado()
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "💰 Total: ${formatearDinero(total)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = comentario,
                onValueChange = viewModel::actualizarComentario,
                label = { Text("\uD83D\uDCCB Notas (opcional)") },
                placeholder = { Text("Ej: Pasear por avenidas") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.agregarPaseo()
                    onPaseoAgregado()
                },
                enabled = viewModel.validacionFormulario(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), // Más alto para ser más visible
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "💾 Guardar Paseo",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Espaciado
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Lista de todos los paseos registrados
@Composable
fun ListaDePaseos(viewModel: ModeloVistaPaseos) {
    val paseos by viewModel.paseos.collectAsState()

    Text(
        text = "📋 Lista de Paseos",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (paseos.isEmpty()) {
        // Mostrar mensaje cuando no hay paseos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\uD83D\uDC15\u200D\uD83E\uDDBA",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "No hay paseos registrados",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Presiona + para agregar un paseo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        // Mostrar lista de paseos
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(paseos) { paseo ->
                TarjetaPaseo(
                    paseo = paseo,
                    onCambiarEstadoPago = { viewModel.actualizarEstaPagado(paseo) },
                    onEliminar = { viewModel.eliminar(paseo) }
                )
            }
        }
    }
}

// Tarjeta individual para cada paseo
@Composable
fun TarjetaPaseo(
    paseo: EntidadPaseoMascota,
    onCambiarEstadoPago: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (paseo.estaPagado) {
                Color(0xFFE8F5E8)
            } else {
                Color(0xFFFFF3E0)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Campos tarjeta
                    Text(
                        text = "${obtenerEmojiTipo(paseo.tipoMascota)} ${paseo.nombreMascota}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "👤 ${paseo.nombreCliente}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "📅 ${formatearFecha(paseo.fechaPaseo)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Cambiar el pago
                AssistChip(
                    onClick = onCambiarEstadoPago,
                    label = {
                        Text(
                            text = if (paseo.estaPagado) "✅ Pagado" else "⏳ Pendiente"
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (paseo.estaPagado) {
                            Color(0xFF4CAF50) // Verde para pagado
                        } else {
                            Color(0xFFFF9800) // Naranja para pendiente
                        },
                        labelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del paseo: duración, precio y total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${paseo.duracionPaseo}h",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${formatearDinero(paseo.precioHora)}/h",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "💰 ${formatearDinero(paseo.montoTotal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Mostrar notas si las hay
            if (paseo.comentario.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📝 ${paseo.comentario}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Btn eliminar
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEliminar,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F) // Rojo para eliminar
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

// dinero a pesos chilenos
fun formatearDinero(cantidad: Double): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return formato.format(cantidad)
}

fun formatearFecha(fecha: Date): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
    return formato.format(fecha)
}

fun obtenerEmojiTipo(tipo: String): String {
    return when (tipo) {
        "Perro" -> "🐕"
        "Gato" -> "🐱"
        "Conejo" -> "🐰"
        else -> "🐾"
    }
}