package org.adaway.ui.prefs

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.adaway.helper.ThemeHelper
import org.adaway.ui.compose.ExpressiveAppContainer
import org.adaway.ui.compose.ExpressiveScaffold
import org.adaway.ui.compose.ExpressiveTopBar

/**
 * This activity is the preferences activity.
 */
class PrefsActivity : AppCompatActivity() {
    private var destination by mutableStateOf(PrefsDestination.MAIN)
    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ThemeHelper.applyTheme(this)
        destination = savedInstanceState
            ?.getString(DESTINATION_KEY)
            ?.let { route -> PrefsDestination.entries.firstOrNull { it.name == route } }
            ?: PrefsDestination.MAIN

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navigateUpInPrefs()) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        supportActionBar?.hide()

        setContent {
            ExpressiveAppContainer {
                PrefsActivityScreen(
                    destination = destination,
                    onNavigateBack = {
                        if (!navigateUpInPrefs()) {
                            finish()
                        }
                    },
                    onNavigate = ::navigateTo,
                    onRequestRecreate = ::recreate
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(DESTINATION_KEY, destination.name)
        super.onSaveInstanceState(outState)
    }

    private fun navigateTo(destination: PrefsDestination) {
        this.destination = destination
    }

    private fun navigateUpInPrefs(): Boolean {
        return if (destination != PrefsDestination.MAIN) {
            navigateTo(PrefsDestination.MAIN)
            true
        } else {
            false
        }
    }

    companion object {
        private const val DESTINATION_KEY = "prefs_destination_key"
    }
}

@Composable
private fun PrefsActivityScreen(
    destination: PrefsDestination,
    onNavigateBack: () -> Unit,
    onNavigate: (PrefsDestination) -> Unit,
    onRequestRecreate: () -> Unit
) {
    ExpressiveScaffold(
        topBar = {
            ExpressiveTopBar(
                title = stringResource(destination.titleRes),
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrefsContent(
                destination = destination,
                onNavigate = onNavigate,
                onRequestRecreate = onRequestRecreate
            )
        }
    }
}
