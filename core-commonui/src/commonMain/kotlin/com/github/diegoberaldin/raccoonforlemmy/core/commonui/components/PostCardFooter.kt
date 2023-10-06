package com.github.diegoberaldin.raccoonforlemmy.core.commonui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.utils.DateTime
import com.github.diegoberaldin.raccoonforlemmy.core.utils.onClick
import com.github.diegoberaldin.raccoonforlemmy.core.utils.toLocalDp
import com.github.diegoberaldin.raccoonforlemmy.resources.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PostCardFooter(
    modifier: Modifier = Modifier,
    separateUpAndDownVotes: Boolean = false,
    comments: Int? = null,
    date: String? = null,
    score: Int = 0,
    upvotes: Int = 0,
    downvotes: Int = 0,
    saved: Boolean = false,
    upVoted: Boolean = false,
    downVoted: Boolean = false,
    options: List<String> = emptyList(),
    onUpVote: (() -> Unit)? = null,
    onDownVote: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onOptionSelected: ((Int) -> Unit)? = null,
) {
    var optionsExpanded by remember { mutableStateOf(false) }
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier.padding(vertical = Spacing.xxxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            val buttonModifier = Modifier.size(28.dp).padding(3.5.dp)
            if (comments != null) {
                Image(
                    modifier = buttonModifier.padding(1.dp).onClick {
                        onReply?.invoke()
                    },
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                )
                Text(
                    modifier = Modifier.padding(end = Spacing.s),
                    text = "$comments",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (date != null) {
                Icon(
                    modifier = buttonModifier.padding(1.dp),
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = date.let {
                        when {
                            it.isEmpty() -> it
                            !it.endsWith("Z") -> {
                                DateTime.getPrettyDate(
                                    iso8601Timestamp = it + "Z",
                                    yearLabel = stringResource(MR.strings.profile_year_short),
                                    monthLabel = stringResource(MR.strings.profile_month_short),
                                    dayLabel = stringResource(MR.strings.profile_day_short),
                                    hourLabel = stringResource(MR.strings.post_hour_short),
                                    minuteLabel = stringResource(MR.strings.post_minute_short),
                                    secondLabel = stringResource(MR.strings.post_second_short),
                                )
                            }

                            else -> {
                                DateTime.getPrettyDate(
                                    iso8601Timestamp = it,
                                    yearLabel = stringResource(MR.strings.profile_year_short),
                                    monthLabel = stringResource(MR.strings.profile_month_short),
                                    dayLabel = stringResource(MR.strings.profile_day_short),
                                    hourLabel = stringResource(MR.strings.post_hour_short),
                                    minuteLabel = stringResource(MR.strings.post_minute_short),
                                    secondLabel = stringResource(MR.strings.post_second_short),
                                )
                            }
                        }
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (options.isNotEmpty()) {
                Icon(
                    modifier = buttonModifier
                        .onGloballyPositioned {
                            optionsOffset = it.positionInParent()
                        }
                        .onClick {
                            optionsExpanded = true
                        },
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = buttonModifier.onClick {
                    onSave?.invoke()
                },
                imageVector = if (!saved) {
                    Icons.Default.BookmarkBorder
                } else {
                    Icons.Default.Bookmark
                },
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = if (saved) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                ),
            )
            Image(
                modifier = buttonModifier
                    .onClick {
                        onUpVote?.invoke()
                    },
                imageVector = Icons.Default.ArrowCircleUp,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = if (upVoted) {
                        MaterialTheme.colorScheme.surfaceTint
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
            Text(
                text = buildString {
                    if (separateUpAndDownVotes) {
                        append(upvotes)
                        append(" / ")
                        append(downvotes)
                    } else {
                        append(score)
                    }
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Image(
                modifier = buttonModifier
                    .onClick {
                        onDownVote?.invoke()
                    },
                imageVector = Icons.Default.ArrowCircleDown,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = if (downVoted) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
        }

        CustomDropDown(
            expanded = optionsExpanded,
            onDismiss = {
                optionsExpanded = false
            },
            offset = DpOffset(
                x = optionsOffset.x.toLocalDp(),
                y = optionsOffset.y.toLocalDp(),
            ),
        ) {
            options.forEachIndexed { idx, text ->
                Text(
                    modifier = Modifier.padding(
                        horizontal = Spacing.m,
                        vertical = Spacing.xs,
                    ).onClick {
                        optionsExpanded = false
                        onOptionSelected?.invoke(idx)
                    },
                    text = text,
                )
            }
        }
    }
}
