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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
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
    //Colores

    val azulHeader = colorResource(id = R.color.azul_header)
    val marronCalido = colorResource(id = R.color.marron_calido)
    val marronClaro = colorResource(id = R.color.marron_claro)

    //Config BD y viewmodel
    val context = LocalContext.current
    val db = BaseDeDatosPaseos.obtenerBD(context)
    val repositorio = RepositorioPaseosMascotas(db.AccesoDatosPaseos())
    val viewModel: ModeloVistaPaseos = viewModel { ModeloVistaPaseos(repositorio) }

    var mostrarOcultarFormulario by remember { mutableStateOf(false) }

    var mostrarFormularioEdicion by remember { mutableStateOf(false) }
    var paseoAEditar by remember { mutableStateOf<EntidadPaseoMascota?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🐕 Control de Paseos",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = marronCalido,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "🐾 Bienvenido/a",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 0.5.sp
                            ),
                            color = marronClaro,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulHeader
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarOcultarFormulario = !mostrarOcultarFormulario }
            ) {
                Icon(
                    imageVector = if (mostrarOcultarFormulario) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (mostrarOcultarFormulario) stringResource(R.string.cerrar) else stringResource(R.string.agregarPaseo)
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

            // Mostrar uno de los tres bloques según el estado
            when {
                mostrarFormularioEdicion && paseoAEditar != null -> {
                    FormularioEditarPaseo(
                        paseo = paseoAEditar!!,
                        viewModel = viewModel,
                        guardarCambios = {
                            mostrarFormularioEdicion = false
                            paseoAEditar = null
                        }
                    )
                }

                mostrarOcultarFormulario -> {
                    FormularioNuevoPaseo(viewModel) {
                        mostrarOcultarFormulario = false
                    }
                }

                else -> {
                    ListaDePaseos(
                        viewModel = viewModel,
                        editar = { paseo ->
                            paseoAEditar = paseo
                            mostrarFormularioEdicion = true
                            mostrarOcultarFormulario = false
                        }
                    )
                }
            }
        }
    }
}
// Tarjeta que muestra las estadísticas de dinero
@Composable
fun TarjetaEstadisticas(viewModel: ModeloVistaPaseos) {
    //Colores
    val azulCard = colorResource(id = R.color.azul_card)
    val verdeClaro = colorResource(id = R.color.verde_claro)
    val naranja = colorResource(id = R.color.naranja)

    val totalGanado by viewModel.totalGanado.collectAsState()
    val totalPendiente by viewModel.totalPendiente.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = azulCard
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.tituloEstadisticas),
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
                        text = stringResource(R.string.ganado),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatearDinero(totalGanado),
                        style = MaterialTheme.typography.headlineSmall,
                        color = verdeClaro,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.pendiente),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = formatearDinero(totalPendiente),
                        style = MaterialTheme.typography.headlineSmall,
                        color = naranja,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.total),
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
    paseoAgregado: () -> Unit
) {
    // Estados del ViewModel
    val nombreMascota by viewModel.nombreMascota.collectAsState()
    val tipoMascota by viewModel.tipoMascota.collectAsState()
    val nombreCliente by viewModel.nombreCliente.collectAsState()
    val duracionPaseo by viewModel.duracionPaseo.collectAsState()
    val precioPorHora by viewModel.precioPorHora.collectAsState()
    val comentario by viewModel.comentario.collectAsState()

    // Colores desde colors.xml
    val azulFocus = colorResource(id = R.color.azul_focus)
    val azulBorder = colorResource(id = R.color.azul_border)
    val azulSuave = colorResource(id = R.color.azul_suave)
    val azulCard = colorResource(id = R.color.azul_card)
    val verdeOk = colorResource(id = R.color.verde_ok)

    // Dropdown
    var expandedTipoMascota by remember { mutableStateOf(false) }
    val tiposMascotas = listOf("Perro", "Gato", "Conejo", "Ave", "Otro")

    // 🎨 Colores de campos en Material 3
    val coloresCampos = TextFieldDefaults.colors(
        focusedIndicatorColor = azulFocus,
        unfocusedIndicatorColor = azulBorder,
        disabledIndicatorColor = azulBorder.copy(alpha = 0.4f),
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = azulFocus,
        errorLabelColor = MaterialTheme.colorScheme.error,
        cursorColor = azulFocus
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = azulSuave)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "➕ Nuevo Paseo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreMascota,
                onValueChange = viewModel::actualizarNombreMascota,
                label = { Text(stringResource(R.string.nombreMascota)) },
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
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
                    label = { Text(stringResource(R.string.tipoMascota)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipoMascota)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = coloresCampos
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

            // 📌 Campo Nombre del Cliente
            OutlinedTextField(
                value = nombreCliente,
                onValueChange = viewModel::actualizarNombreCliente,
                label = { Text(stringResource(R.string.nombreCliente)) },
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = duracionPaseo,
                    onValueChange = viewModel::actualizarDuracionPaseo,
                    label = { Text(stringResource(R.string.horas)) },
                    modifier = Modifier.weight(1f),
                    colors = coloresCampos
                )
                OutlinedTextField(
                    value = precioPorHora,
                    onValueChange = viewModel::actualizarPrecioPorHora,
                    label = { Text(stringResource(R.string.precioHora)) },
                    modifier = Modifier.weight(1f),
                    colors = coloresCampos
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (duracionPaseo.isNotEmpty() && precioPorHora.isNotEmpty()) {
                val total = viewModel.calcularTotalGanado()
                Card(colors = CardDefaults.cardColors(containerColor = azulCard)) {
                    Text(
                        text = stringResource(R.string.totalFormateado, formatearDinero(total)),
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
                label = { Text(stringResource(R.string.notas)) },
                placeholder = { Text(stringResource(R.string.placeholderNotas)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                colors = coloresCampos
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.agregarPaseo()
                    paseoAgregado()
                },
                enabled = viewModel.validacionFormulario(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = verdeOk,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btnGuardarPaseo),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioEditarPaseo(
    paseo: EntidadPaseoMascota,
    viewModel: ModeloVistaPaseos,
    guardarCambios: () -> Unit
) {
    // Estados locales con los datos actuales del paseo
    var nombreMascota by remember { mutableStateOf(paseo.nombreMascota) }
    var tipoMascota by remember { mutableStateOf(paseo.tipoMascota) }
    var nombreCliente by remember { mutableStateOf(paseo.nombreCliente) }
    var duracionPaseo by remember { mutableStateOf(paseo.duracionPaseo.toString()) }
    var precioPorHora by remember { mutableStateOf(paseo.precioHora.toString()) }
    var comentario by remember { mutableStateOf(paseo.comentario) }

    var expandedTipoMascota by remember { mutableStateOf(false) }
    val tiposMascotas = listOf("Perro", "Gato", "Conejo", "Ave", "Otro")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.editarPaseo),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreMascota,
                onValueChange = { nombreMascota = it },
                label = { Text(stringResource(R.string.nombreMascota)) },
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
                    label = { Text(stringResource(R.string.tipoMascota)) },
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
                                tipoMascota = tipo
                                expandedTipoMascota = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombreCliente,
                onValueChange = { nombreCliente = it },
                label = { Text(stringResource(R.string.nombreCliente)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = duracionPaseo,
                    onValueChange = { duracionPaseo = it },
                    label = { Text(stringResource(R.string.horas)) },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = precioPorHora,
                    onValueChange = { precioPorHora = it },
                    label = { Text(stringResource(R.string.precioHora)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Monto total
            val duracion = duracionPaseo.toDoubleOrNull() ?: 0.0
            val precio = precioPorHora.toDoubleOrNull() ?: 0.0
            val total = duracion * precio

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = stringResource(R.string.totalFormateado, formatearDinero(total)),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text(stringResource(R.string.notas)) },
                placeholder = { Text(stringResource(R.string.placeholderNotas)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.editarPaseo(
                        paseo.copy(
                            nombreMascota = nombreMascota,
                            tipoMascota = tipoMascota,
                            nombreCliente = nombreCliente,
                            duracionPaseo = duracion,
                            precioHora = precio,
                            montoTotal = total,
                            comentario = comentario
                        )
                    )
                    guardarCambios()
                },
                enabled = nombreMascota.isNotBlank() && nombreCliente.isNotBlank() &&
                        duracionPaseo.toDoubleOrNull() != null && precioPorHora.toDoubleOrNull() != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.guardarCambios),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Lista de todos los paseos registrados
@Composable
fun ListaDePaseos(
    viewModel: ModeloVistaPaseos,
    editar: (EntidadPaseoMascota) -> Unit
) {
    val paseos by viewModel.paseos.collectAsState()

    Text(
        text = stringResource(R.string.listaPaseos),
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
                containerColor = Color(0xFFC8E6C9)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.perritoCorrea),
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = stringResource(R.string.mensajeSinPaseos),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.mensajeAgregarPaseo),
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
                    cambiarEstadoPago = { viewModel.actualizarEstaPagado(paseo) },
                    editar = { editar(paseo) },
                    eliminar = { viewModel.eliminar(paseo) }
                )
            }
        }
    }
}

// Tarjeta individual para cada paseo
@Composable
fun TarjetaPaseo(
    paseo: EntidadPaseoMascota,
    cambiarEstadoPago: () -> Unit,
    editar: () -> Unit,
    eliminar: () -> Unit
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
                    onClick = cambiarEstadoPago,
                    label = {
                        Text(
                            text = if (paseo.estaPagado) stringResource(R.string.pagado) else stringResource(R.string.noPagado)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (paseo.estaPagado) {
                            Color(0xFF4CAF50) // Verde
                        } else {
                            Color(0xFFFF9800) // Naranja
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

            // Btn editar
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { editar() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.editar),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.editar))
                }

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(
                    onClick = eliminar,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.eliminar),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.eliminar))
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
        "Ave" -> "🦜"
        else -> "🐾"
    }
}