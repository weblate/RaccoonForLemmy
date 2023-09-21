package com.github.diegoberaldin.raccoonforlemmy.core.commonui.communitydetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.github.diegoberaldin.racconforlemmy.core.utils.onClick
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.bindToLifecycle
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.communityInfo.CommunityInfoScreen
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.components.CommunityHeader
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.components.PostCard
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.components.SwipeableCard
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.createcomment.CreateCommentScreen
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.createpost.CreatePostScreen
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.di.getCommunityDetailViewModel
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.di.getNavigationCoordinator
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.image.ZoomableImageScreen
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.instanceinfo.InstanceInfoScreen
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.modals.SortBottomSheet
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.postdetail.PostDetailScreen
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.userdetail.UserDetailScreen
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenterContractKeys
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.di.getNotificationCenter
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.CommunityModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.SortType
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.toIcon
import com.github.diegoberaldin.raccoonforlemmy.resources.MR
import dev.icerock.moko.resources.compose.stringResource

class CommunityDetailScreen(
    private val community: CommunityModel,
    private val otherInstance: String = "",
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = rememberScreenModel(community.id.toString() + otherInstance) {
            getCommunityDetailViewModel(
                community = community,
                otherInstance = otherInstance,
            )
        }
        model.bindToLifecycle(key)
        val uiState by model.uiState.collectAsState()
        val navigator = remember { getNavigationCoordinator().getRootNavigator() }
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val isOnOtherInstance = otherInstance.isNotEmpty()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val isFabVisible = remember { mutableStateOf(true) }
        val fabNestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (available.y < -1) {
                        isFabVisible.value = false
                    }
                    if (available.y > 1) {
                        isFabVisible.value = true
                    }
                    return Offset.Zero
                }
            }
        }
        val notificationCenter = remember { getNotificationCenter() }
        DisposableEffect(key) {
            onDispose {
                notificationCenter.removeObserver(key)
            }
        }

        val stateCommunity = uiState.community
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            .padding(Spacing.xs),
            topBar = {
                val communityName = stateCommunity.name
                val communityHost = stateCommunity.host
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            text = buildString {
                                append(communityName)
                                if (communityHost.isNotEmpty()) {
                                    append("@$communityHost")
                                }
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    actions = {
                        Image(
                            modifier = Modifier.onClick {
                                val sheet = SortBottomSheet(
                                    expandTop = true,
                                )
                                notificationCenter.addObserver({
                                    (it as? SortType)?.also { sortType ->
                                        model.reduce(
                                            CommunityDetailMviModel.Intent.ChangeSort(
                                                sortType
                                            )
                                        )
                                    }
                                }, key, NotificationCenterContractKeys.ChangeSortType)
                                bottomSheetNavigator.show(sheet)
                            },
                            imageVector = uiState.sortType.toIcon(),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        )
                    },
                    navigationIcon = {
                        Image(
                            modifier = Modifier.onClick {
                                navigator?.pop()
                            },
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        )
                    },
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = isFabVisible.value,
                    enter = slideInVertically(
                        initialOffsetY = { it * 2 },
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it * 2 },
                    ),
                ) {
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape,
                        onClick = {
                            val screen = CreatePostScreen(
                                communityId = stateCommunity.id,
                            )
                            notificationCenter.addObserver({
                                model.reduce(CommunityDetailMviModel.Intent.Refresh)
                            }, key, NotificationCenterContractKeys.PostCreated)
                            bottomSheetNavigator.show(screen)
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }) { padding ->
            if (uiState.currentUserId != null) {
                val pullRefreshState = rememberPullRefreshState(uiState.refreshing, {
                    model.reduce(CommunityDetailMviModel.Intent.Refresh)
                })
                Box(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                        .nestedScroll(fabNestedScrollConnection).padding(padding)
                        .pullRefresh(pullRefreshState),
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        item {
                            CommunityHeader(
                                community = stateCommunity,
                                isOnOtherInstance = isOnOtherInstance,
                                onOpenCommunityInfo = {
                                    bottomSheetNavigator.show(
                                        CommunityInfoScreen(stateCommunity),
                                    )
                                },
                                onOpenInstanceInfo = {
                                    navigator?.push(
                                        InstanceInfoScreen(
                                            url = stateCommunity.instanceUrl,
                                        ),
                                    )
                                },
                                onSubscribeButtonClicked = {
                                    when (stateCommunity.subscribed) {
                                        true -> model.reduce(CommunityDetailMviModel.Intent.Unsubscribe)
                                        false -> model.reduce(CommunityDetailMviModel.Intent.Subscribe)
                                        else -> Unit
                                    }
                                },
                            )
                        }
                        itemsIndexed(uiState.posts) { idx, post ->
                            SwipeableCard(
                                modifier = Modifier.fillMaxWidth(),
                                enabled = uiState.swipeActionsEnabled,
                                directions = if (isOnOtherInstance) {
                                    emptySet()
                                } else {
                                    setOf(
                                        DismissDirection.StartToEnd,
                                        DismissDirection.EndToStart,
                                    )
                                },
                                backgroundColor = {
                                    when (it) {
                                        DismissValue.DismissedToStart -> MaterialTheme.colorScheme.secondary
                                        DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.tertiary
                                        else -> Color.Transparent
                                    }
                                },
                                swipeContent = { direction ->
                                    val icon = when (direction) {
                                        DismissDirection.StartToEnd -> Icons.Default.ArrowCircleDown
                                        DismissDirection.EndToStart -> Icons.Default.ArrowCircleUp
                                    }
                                    val (iconModifier, iconTint) = when {
                                        direction == DismissDirection.StartToEnd && post.myVote < 0 -> {
                                            Modifier.background(
                                                color = Color.Transparent,
                                                shape = CircleShape,
                                            ) to MaterialTheme.colorScheme.onTertiary
                                        }

                                        direction == DismissDirection.StartToEnd -> {
                                            Modifier.background(
                                                color = MaterialTheme.colorScheme.onTertiary,
                                                shape = CircleShape,
                                            ) to MaterialTheme.colorScheme.tertiary
                                        }

                                        direction == DismissDirection.EndToStart && post.myVote > 0 -> {
                                            Modifier.background(
                                                color = Color.Transparent,
                                                shape = CircleShape,
                                            ) to MaterialTheme.colorScheme.onSecondary
                                        }

                                        else -> {
                                            Modifier.background(
                                                color = MaterialTheme.colorScheme.onSecondary,
                                                shape = CircleShape,
                                            ) to MaterialTheme.colorScheme.secondary
                                        }
                                    }
                                    Icon(
                                        modifier = iconModifier,
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                    )
                                },
                                onGestureBegin = {
                                    model.reduce(CommunityDetailMviModel.Intent.HapticIndication)
                                },
                                onDismissToStart = {
                                    model.reduce(
                                        CommunityDetailMviModel.Intent.UpVotePost(idx),
                                    )
                                },
                                onDismissToEnd = {
                                    model.reduce(
                                        CommunityDetailMviModel.Intent.DownVotePost(idx),
                                    )
                                },
                                content = {
                                    PostCard(
                                        modifier = Modifier.onClick {
                                            navigator?.push(
                                                PostDetailScreen(post),
                                            )
                                        },
                                        onOpenCreator = { user ->
                                            navigator?.push(
                                                UserDetailScreen(user),
                                            )
                                        },
                                        post = post,
                                        options = buildList {
                                            add(stringResource(MR.strings.post_action_share))
                                            if (post.creator?.id == uiState.currentUserId) {
                                                add(stringResource(MR.strings.post_action_edit))
                                                add(stringResource(MR.strings.comment_action_delete))
                                            }
                                        },
                                        blurNsfw = when {
                                            stateCommunity.nsfw -> false
                                            else -> uiState.blurNsfw
                                        },
                                        onUpVote = if (isOnOtherInstance) {
                                            null
                                        } else {
                                            {
                                                model.reduce(
                                                    CommunityDetailMviModel.Intent.UpVotePost(
                                                        index = idx,
                                                        feedback = true,
                                                    ),
                                                )
                                            }
                                        },
                                        onDownVote = if (isOnOtherInstance) {
                                            null
                                        } else {
                                            {
                                                model.reduce(
                                                    CommunityDetailMviModel.Intent.DownVotePost(
                                                        index = idx,
                                                        feedback = true,
                                                    ),
                                                )
                                            }
                                        },
                                        onSave = if (isOnOtherInstance) {
                                            null
                                        } else {
                                            {
                                                model.reduce(
                                                    CommunityDetailMviModel.Intent.SavePost(
                                                        index = idx,
                                                        feedback = true,
                                                    ),
                                                )
                                            }
                                        },
                                        onReply = {
                                            val screen = CreateCommentScreen(
                                                originalPost = post,
                                            )
                                            notificationCenter.addObserver({
                                                model.reduce(CommunityDetailMviModel.Intent.Refresh)
                                            }, key, NotificationCenterContractKeys.CommentCreated)
                                            bottomSheetNavigator.show(screen)
                                        },
                                        onImageClick = { url ->
                                            navigator?.push(
                                                ZoomableImageScreen(url),
                                            )
                                        },
                                        onOptionSelected = { optionIdx ->
                                            when (optionIdx) {
                                                2 -> model.reduce(
                                                    CommunityDetailMviModel.Intent.DeletePost(
                                                        post.id
                                                    )
                                                )

                                                1 -> {
                                                    notificationCenter.addObserver(
                                                        {
                                                            model.reduce(CommunityDetailMviModel.Intent.Refresh)
                                                        },
                                                        key,
                                                        NotificationCenterContractKeys.PostCreated
                                                    )
                                                    bottomSheetNavigator.show(
                                                        CreatePostScreen(
                                                            editedPost = post,
                                                        )
                                                    )
                                                }

                                                else -> model.reduce(
                                                    CommunityDetailMviModel.Intent.SharePost(idx)
                                                )
                                            }
                                        }
                                    )
                                },
                            )
                        }
                        item {
                            if (!uiState.loading && !uiState.refreshing && uiState.canFetchMore) {
                                model.reduce(CommunityDetailMviModel.Intent.LoadNextPage)
                            }
                            if (uiState.loading && !uiState.refreshing) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(Spacing.xs),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(25.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(Spacing.s))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = uiState.refreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
