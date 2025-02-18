package com.github.diegoberaldin.raccoonforlemmy.unit.managesubscriptions.di

import com.github.diegoberaldin.raccoonforlemmy.unit.managesubscriptions.ManageSubscriptionsMviModel
import com.github.diegoberaldin.raccoonforlemmy.unit.managesubscriptions.ManageSubscriptionsViewModel
import com.github.diegoberaldin.raccoonforlemmy.unit.multicommunity.di.multiCommunityModule
import org.koin.dsl.module

val manageSubscriptionsModule =
    module {
        includes(multiCommunityModule)

        factory<ManageSubscriptionsMviModel> {
            ManageSubscriptionsViewModel(
                identityRepository = get(),
                communityRepository = get(),
                accountRepository = get(),
                multiCommunityRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
                settingsRepository = get(),
                favoriteCommunityRepository = get(),
                siteRepository = get(),
                communityPaginationManager = get(),
            )
        }
    }
