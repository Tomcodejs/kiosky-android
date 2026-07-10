package com.kiosky.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kiosky.launcher.LauncherViewModel
import com.kiosky.launcher.data.KioskyButton
import com.kiosky.launcher.ui.theme.BackgroundBottom
import com.kiosky.launcher.ui.theme.BackgroundTop

@Composable
fun LauncherScreen(viewModel: LauncherViewModel) {
    val buttons by viewModel.buttons.collectAsState()
    val editMode by viewModel.editMode.collectAsState()
    val context = LocalContext.current

    var editingButton by remember { mutableStateOf<KioskyButton?>(null) }

    BoxWithConstraints(
        modifier = Modifier
            .background(Brush.verticalGradient(listOf(BackgroundTop, BackgroundBottom)))
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val density = androidx.compose.ui.platform.LocalDensity.current

        buttons.forEach { button ->
            val xPx = button.x * widthPx
            val yPx = button.y * heightPx

            GlassButton(
                button = button,
                modifier = Modifier
                    .offset(
                        x = with(density) { xPx.toDp() },
                        y = with(density) { yPx.toDp() }
                    )
                    .size(button.widthDp.dp, button.heightDp.dp)
                    .then(
                        if (editMode) {
                            Modifier.pointerInput(button.id) {
                                var accumulatedOffset = androidx.compose.ui.geometry.Offset.Zero
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    accumulatedOffset += dragAmount
                                    val newX = (xPx + accumulatedOffset.x) / widthPx
                                    val newY = (yPx + accumulatedOffset.y) / heightPx
                                    viewModel.moveButton(button.id, newX, newY)
                                }
                            }
                        } else Modifier
                    ),
                onClick = {
                    if (editMode) {
                        editingButton = button
                    } else {
                        button.packageName?.let { pkg ->
                            context.packageManager.getLaunchIntentForPackage(pkg)?.let {
                                context.startActivity(it)
                            }
                        }
                    }
                },
                onLongClick = { editingButton = button }
            )
        }

        // Bouton flottant : bascule mode édition / ajoute un bouton
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            FloatingActionButton(
                onClick = { viewModel.toggleEditMode() },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(if (editMode) Icons.Rounded.Check else Icons.Rounded.Edit, contentDescription = null)
            }
            if (editMode) {
                FloatingActionButton(
                    onClick = { viewModel.addButton() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 76.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    }

    editingButton?.let { current ->
        EditButtonDialog(
            button = current,
            onDismiss = { editingButton = null },
            onDelete = {
                viewModel.deleteButton(current.id)
                editingButton = null
            },
            onSave = {
                viewModel.updateButton(it)
                editingButton = null
            }
        )
    }
}
