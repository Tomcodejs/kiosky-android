package com.kiosky.launcher.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kiosky.launcher.data.KioskyButton
import com.kiosky.launcher.ui.theme.GlassHighlight
import com.kiosky.launcher.ui.theme.GlassRim

/**
 * Rendu "Liquid Glass" : plusieurs calques superposés pour simuler un verre
 * dépoli translucide avec reflet en haut et teinte de couleur en profondeur.
 * - Calque 1 : flou d'arrière-plan (glassmorphism)
 * - Calque 2 : dégradé de teinte + transparence
 * - Calque 3 : liseré lumineux (rim light) sur le bord supérieur
 * - Calque 4 : icône + libellé
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassButton(
    button: KioskyButton,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val baseColor = runCatching { Color(android.graphics.Color.parseColor(button.colorHex)) }
        .getOrDefault(Color(0xFF5B8CFF))
    val shape = RoundedCornerShape(button.cornerRadiusDp.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .background(
                Brush.verticalGradient(
                    listOf(
                        baseColor.copy(alpha = 0.38f),
                        baseColor.copy(alpha = 0.16f)
                    )
                )
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(GlassHighlight, Color.Transparent),
                    start = Offset(0f, 0f),
                    end = Offset(0f, 260f)
                )
            )
            .border(width = 1.dp, color = GlassRim, shape = shape)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        // Voile flouté supplémentaire pour la profondeur du verre
        Box(
            Modifier
                .fillMaxSize()
                .blur(18.dp)
                .background(baseColor.copy(alpha = 0.05f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (button.customIconUri != null) {
                    AsyncImage(
                        model = button.customIconUri,
                        contentDescription = button.label,
                        modifier = Modifier.size((button.widthDp * 0.32f).dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Apps,
                        contentDescription = button.label,
                        tint = Color.White,
                        modifier = Modifier.size((button.widthDp * 0.32f).dp)
                    )
                }
                Text(
                    text = button.label,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}
