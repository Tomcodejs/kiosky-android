package com.kiosky.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kiosky_prefs")
private val BUTTONS_KEY = stringPreferencesKey("buttons_json")

/**
 * Gère la lecture/écriture de la liste des boutons dans le DataStore local,
 * sous forme de JSON. Aucune donnée ne quitte l'appareil.
 */
class ButtonRepository(private val context: Context) {

    private val gson = Gson()

    val buttons: Flow<List<KioskyButton>> = context.dataStore.data.map { prefs ->
        val json = prefs[BUTTONS_KEY] ?: return@map defaultButtons()
        runCatching {
            val type = object : TypeToken<List<KioskyButton>>() {}.type
            gson.fromJson<List<KioskyButton>>(json, type)
        }.getOrElse { defaultButtons() }
    }

    suspend fun save(buttons: List<KioskyButton>) {
        val json = gson.toJson(buttons)
        context.dataStore.edit { it[BUTTONS_KEY] = json }
    }

    private fun defaultButtons(): List<KioskyButton> = listOf(
        KioskyButton(
            label = "Appli Internet",
            colorHex = "#5B8CFF",
            x = 0.08f,
            y = 0.12f
        ),
        KioskyButton(
            label = "Paramètres",
            colorHex = "#8C5BFF",
            x = 0.08f,
            y = 0.42f
        )
    )
}
