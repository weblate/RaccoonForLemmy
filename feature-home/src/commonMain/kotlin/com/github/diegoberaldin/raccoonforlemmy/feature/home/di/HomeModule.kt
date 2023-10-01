package com.github.diegoberaldin.raccoonforlemmy.feature.home.di

import com.github.diegoberaldin.raccoonforlemmy.core.architecture.DefaultMviModel
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.di.commonUiModule
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.di.repositoryModule
import com.github.diegoberaldin.raccoonforlemmy.feature.home.postlist.PostListMviModel
import com.github.diegoberaldin.raccoonforlemmy.feature.home.postlist.PostListViewModel
import org.koin.dsl.module

val homeTabModule = module {
    includes(
        repositoryModule,
        commonUiModule,
    )
    factory {
        PostListViewModel(
            mvi = DefaultMviModel(PostListMviModel.UiState()),
            postRepository = get(),
            apiConfigRepository = get(),
            identityRepository = get(),
            siteRepository = get(),
            themeRepository = get(),
            shareHelper = get(),
            keyStore = get(),
            notificationCenter = get(),
            hapticFeedback = get(),
        )
    }
}
