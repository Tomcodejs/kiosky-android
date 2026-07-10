package com.kiosky.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiosky.launcher.data.ButtonRepository
import com.kiosky.launcher.ui.LauncherScreen
import com.kiosky.launcher.ui.theme.KioskyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ButtonRepository(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LauncherViewModel(repository) as T
            }
        }

        setContent {
            KioskyTheme {
                val viewModel: LauncherViewModel = viewModel(factory = factory)
                LauncherScreen(viewModel = viewModel)
            }
        }
    }

    // Kiosky agit comme écran d'accueil : on ignore le bouton "retour"
    // pour éviter de quitter le launcher accidentellement.
    override fun onBackPressed() {
        // volontairement vide
    }
}
