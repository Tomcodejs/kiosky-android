package com.kiosky.launcher.data

import java.util.UUID

/**
 * Représente un bouton programmable de l'écran d'accueil Kiosky.
 *
 * @param x position horizontale, en fraction de la largeur d'écran (0f à 1f)
 * @param y position verticale, en fraction de la hauteur d'écran (0f à 1f)
 * @param widthDp largeur du bouton en dp
 * @param heightDp hauteur du bouton en dp
 * @param colorHex couleur d'accentuation du verre, ex "#3B82F6"
 * @param packageName application Android lancée au clic (null si non configuré)
 * @param customIconUri Uri (content://) d'une icône personnalisée choisie par l'utilisateur
 */
data class KioskyButton(
    val id: String = UUID.randomUUID().toString(),
    val label: String = "Nouveau bouton",
    val packageName: String? = null,
    val customIconUri: String? = null,
    val colorHex: String = "#5B8CFF",
    val x: Float = 0.1f,
    val y: Float = 0.1f,
    val widthDp: Float = 140f,
    val heightDp: Float = 140f,
    val cornerRadiusDp: Float = 32f
)
