package cl.duoc.electromechanics.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.electromechanics.data.sync.EstadoSync
import cl.duoc.electromechanics.domain.util.DateUtils
import cl.duoc.electromechanics.ui.theme.InfoBlue
import cl.duoc.electromechanics.ui.theme.InfoContainer
import cl.duoc.electromechanics.ui.theme.SuccessContainer
import cl.duoc.electromechanics.ui.theme.SuccessGreen
import cl.duoc.electromechanics.ui.theme.WarningContainer
import cl.duoc.electromechanics.ui.theme.WarningOrange

private data class SyncUi(val texto: String, val icono: ImageVector, val fondo: Color, val color: Color)

@Composable
fun SyncBanner(estado: EstadoSync, onSincronizar: () -> Unit, modifier: Modifier = Modifier) {
    val sincronizando = estado is EstadoSync.Sincronizando
    val ui = when (estado) {
        EstadoSync.Inactivo -> SyncUi("Conectando con el servidor", Icons.Filled.CloudQueue, InfoContainer, InfoBlue)
        EstadoSync.Sincronizando -> SyncUi("Sincronizando con el servidor...", Icons.Filled.Sync, InfoContainer, InfoBlue)
        is EstadoSync.Sincronizado -> SyncUi(
            "Sincronizado a las ${DateUtils.formatHora(estado.hora)}",
            Icons.Filled.CloudDone,
            SuccessContainer,
            SuccessGreen
        )
        is EstadoSync.SinConexion -> SyncUi(
            if (estado.pendientes > 0) "Sin conexión · ${estado.pendientes} cambio(s) por enviar"
            else "Sin conexión con el servidor",
            Icons.Filled.CloudOff,
            WarningContainer,
            WarningOrange
        )
    }

    val fondoAnimado by animateColorAsState(ui.fondo, label = "fondoSync")
    val giro = rememberInfiniteTransition(label = "giroSync")
    val angulo by giro.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "anguloSync"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(fondoAnimado)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            ui.icono,
            contentDescription = null,
            tint = ui.color,
            modifier = Modifier
                .size(20.dp)
                .rotate(if (sincronizando) -angulo else 0f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            ui.texto,
            color = ui.color,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onSincronizar, enabled = !sincronizando) {
            Text("Sincronizar", color = ui.color)
        }
    }
}
