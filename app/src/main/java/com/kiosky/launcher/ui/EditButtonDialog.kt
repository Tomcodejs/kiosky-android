package com.kiosky.launcher.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kiosky.launcher.data.KioskyButton
import com.kiosky.launcher.ui.theme.PresetSwatches

@Composable
fun EditButtonDialog(
    button: KioskyButton,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (KioskyButton) -> Unit
) {
    var label by remember { mutableStateOf(button.label) }
    var colorHex by remember { mutableStateOf(button.colorHex) }
    var sizeDp by remember { mutableStateOf(button.widthDp) }
    var packageName by remember { mutableStateOf(button.packageName) }
    var iconUri by remember { mutableStateOf(button.customIconUri) }
    var showAppPicker by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { iconUri = it.toString() } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Personnaliser le bouton") },
        text = {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Nom du bouton") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Couleur", modifier = Modifier.padding(top = 14.dp, bottom = 6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PresetSwatches.forEach { hex ->
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Row {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(c, CircleShape)
                                    .border(
                                        width = if (colorHex == hex) 3.dp else 1.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { colorHex = hex }
                            )
                        }
                    }
                }

                Text("Taille : ${sizeDp.toInt()} dp", modifier = Modifier.padding(top = 14.dp))
                Slider(
                    value = sizeDp,
                    onValueChange = { sizeDp = it },
                    valueRange = 90f..260f
                )

                Text("Icône personnalisée", modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
                OutlinedButton(onClick = { imagePicker.launch("image/*") }) {
                    Text(if (iconUri != null) "Icône choisie ✓ (changer)" else "Choisir une image")
                }

                Text("Application lancée", modifier = Modifier.padding(top = 14.dp, bottom = 4.dp))
                OutlinedButton(onClick = { showAppPicker = true }) {
                    Text(packageName ?: "Choisir une application…")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    button.copy(
                        label = label,
                        colorHex = colorHex,
                        widthDp = sizeDp,
                        heightDp = sizeDp,
                        packageName = packageName,
                        customIconUri = iconUri
                    )
                )
            }) { Text("Enregistrer") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) { Text("Supprimer") }
                TextButton(onClick = onDismiss) { Text("Annuler") }
            }
        }
    )

    if (showAppPicker) {
        AppPickerDialog(
            onDismiss = { showAppPicker = false },
            onAppSelected = { app ->
                packageName = app.packageName
                if (label.isBlank() || label == "Nouveau bouton") label = app.label
                showAppPicker = false
            }
        )
    }
}
