package com.chucklingkoala.stagehand.presentation.urldetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.presentation.components.CategoryPickerSheet
import com.chucklingkoala.stagehand.presentation.components.EpisodePickerSheet
import com.chucklingkoala.stagehand.presentation.components.StatusPills
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import com.chucklingkoala.stagehand.util.DateFormatter
import org.koin.androidx.compose.get
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlDetailScreen(
    urlId: Int,
    onNavigateBack: () -> Unit,
    viewModel: UrlDetailViewModel = get(parameters = { parametersOf(urlId) })
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showMenu by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showEpisodeSheet by remember { mutableStateOf(false) }

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.url?.title ?: stringResource(R.string.url_detail_title),
                        color = StagehandColors.TextPrimary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StagehandColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.menu),
                            tint = StagehandColors.TextPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.copy_url)) },
                            onClick = {
                                state.url?.url?.let { url ->
                                    clipboardManager.setText(AnnotatedString(url))
                                }
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.share_url)) },
                            onClick = {
                                state.url?.url?.let { url ->
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, url)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share URL"))
                                }
                                showMenu = false
                            }
                        )
                        state.url?.discordMessageLink?.let { discordLink ->
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.view_discord)) },
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(discordLink))
                                    context.startActivity(intent)
                                    showMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StagehandColors.BackgroundSecondary,
                    titleContentColor = StagehandColors.TextPrimary
                )
            )
        },
        containerColor = StagehandColors.BackgroundPrimary
    ) { padding ->
        if (state.isLoading && state.url == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StagehandColors.AccentColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.linkPreview?.let { preview ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = StagehandColors.CardBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            preview.imageUrl?.let { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Preview Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            preview.title?.let { title ->
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = StagehandColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            preview.description?.let { description ->
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = StagehandColors.TextSecondary,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = StagehandColors.CardBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "URL:",
                            style = MaterialTheme.typography.labelMedium,
                            color = StagehandColors.TextSecondary
                        )
                        Text(
                            text = state.url?.url ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StagehandColors.LinkColor
                        )

                        Button(
                            onClick = {
                                state.url?.url?.let { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StagehandColors.ButtonPrimary
                            )
                        ) {
                            Text(stringResource(R.string.open_in_browser))
                        }

                        Divider(color = StagehandColors.BorderColor)

                        Text(
                            text = stringResource(R.string.posted_by, state.url?.postedBy ?: ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = StagehandColors.TextPrimary
                        )

                        Text(
                            text = "Posted: ${DateFormatter.formatFullDate(state.url?.postedAt ?: "")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StagehandColors.TextSecondary
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = StagehandColors.CardBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.category_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = StagehandColors.TextSecondary
                        )
                        OutlinedButton(
                            onClick = { showCategorySheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.categories.firstOrNull { it.id == state.selectedCategoryId }?.name
                                    ?: stringResource(R.string.uncategorized_label),
                                color = StagehandColors.TextPrimary
                            )
                        }

                        Text(
                            text = stringResource(R.string.episode_label_detail),
                            style = MaterialTheme.typography.labelMedium,
                            color = StagehandColors.TextSecondary
                        )
                        OutlinedButton(
                            onClick = { showEpisodeSheet = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val ep = state.episodes.firstOrNull { it.id == state.selectedEpisodeId }
                            Text(
                                text = ep?.let { stringResource(R.string.episode_label, it.episodeNumber) }
                                    ?: stringResource(R.string.no_episode_label),
                                color = StagehandColors.TextPrimary
                            )
                        }

                        Divider(color = StagehandColors.BorderColor)

                        Text(
                            text = stringResource(R.string.status_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = StagehandColors.TextSecondary
                        )
                        StatusPills(
                            current = state.selectedStatus,
                            onChange = { viewModel.onEvent(UrlDetailEvent.SelectStatus(it)) }
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.covered_label),
                                color = StagehandColors.TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = state.selectedCovered,
                                onCheckedChange = {
                                    viewModel.onEvent(UrlDetailEvent.SetCovered(it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = StagehandColors.AccentColor,
                                    checkedTrackColor = StagehandColors.AccentColor.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.onEvent(UrlDetailEvent.SaveChanges) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isSaving,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StagehandColors.ButtonSuccess
                            )
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = StagehandColors.TextPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.save_changes),
                                    color = StagehandColors.BackgroundPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCategorySheet) {
        CategoryPickerSheet(
            categories = state.categories,
            currentCategoryId = state.selectedCategoryId,
            onPick = { id ->
                viewModel.onEvent(UrlDetailEvent.SelectCategory(id))
                showCategorySheet = false
            },
            onDismiss = { showCategorySheet = false }
        )
    }

    if (showEpisodeSheet) {
        EpisodePickerSheet(
            episodes = state.episodes,
            currentEpisodeId = state.selectedEpisodeId,
            onPick = { id ->
                viewModel.onEvent(UrlDetailEvent.SelectEpisode(id))
                showEpisodeSheet = false
            },
            onDismiss = { showEpisodeSheet = false }
        )
    }
}
