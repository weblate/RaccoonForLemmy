package com.github.diegoberaldin.raccoonforlemmy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.github.diegoberaldin.raccoonforlemmy.core_appearance.theme.AppTheme
import com.github.diegoberaldin.raccoonforlemmy.core_preferences.KeyStoreKeys
import com.github.diegoberaldin.raccoonforlemmy.core_preferences.di.getTemporaryKeyStore
import com.github.diegoberaldin.raccoonforlemmy.feature_home.HomeTab
import com.github.diegoberaldin.raccoonforlemmy.feature_inbox.InboxTab
import com.github.diegoberaldin.raccoonforlemmy.feature_profile.ProfileTab
import com.github.diegoberaldin.raccoonforlemmy.feature_search.SearchTab
import com.github.diegoberaldin.raccoonforlemmy.feature_settings.SettingsTab
import com.github.diegoberaldin.raccoonforlemmy.resources.MR
import com.github.diegoberaldin.raccoonforlemmy.resources.getLanguageRepository
import com.github.diegoberaldin.raccoonforlemmy.ui.navigation.TabNavigationItem
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val keyStore = remember { getTemporaryKeyStore() }
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = runBlocking {
        keyStore.get(KeyStoreKeys.EnableDarkTheme, systemDarkTheme)
    }

    val defaultLocale = stringResource(MR.strings.lang)
    val lang = runBlocking {
        keyStore.get(KeyStoreKeys.Locale, defaultLocale)
    }
    val languageRepository = remember { getLanguageRepository() }
    languageRepository.changeLanguage(lang)

    val scope = rememberCoroutineScope()
    languageRepository.currentLanguage.onEach { lang ->
        StringDesc.localeType = StringDesc.LocaleType.Custom(lang)
    }.launchIn(scope)

    AppTheme(
        darkTheme = darkTheme
    ) {
        val lang by languageRepository.currentLanguage.collectAsState()
        LaunchedEffect(lang) {}

        TabNavigator(HomeTab) {
            Scaffold(
                content = {
                    CurrentTab()
                },
                bottomBar = {
                    BottomAppBar {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(SearchTab)
                        TabNavigationItem(ProfileTab)
                        TabNavigationItem(InboxTab)
                        TabNavigationItem(SettingsTab)
                    }
                }
            )
        }
    }
}
