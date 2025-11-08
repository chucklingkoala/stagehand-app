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
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import com.chucklingkoala.stagehand.util.DateFormatter
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlDetailScreen(
    urlId: Int,
    onNavigateBack: () -> Unit,
    viewModel: UrlDetailViewModel = koinViewModel(parameters = { parametersOf(urlId) })
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showMenu by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

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
                            contentDescription = "Menu",
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
                // Link Preview Card
                state.linkPreview?.let { preview ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = StagehandColors.CardBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
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

                // URL Info Card
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

                // Edit Section
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
                        // Category Dropdown
                        Text(
                            text = stringResource(R.string.category_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = StagehandColors.TextSecondary
                        )

                        ExposedDropdownMenuBox(
                            expanded = showCategoryDropdown,
                            onExpandedChange = { showCategoryDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = state.categories.find { it.id == state.selectedCategoryId }?.name
                                    ?: "Uncategorized",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = StagehandColors.InputBackground,
                                    unfocusedContainerColor = StagehandColors.InputBackground,
                                    focusedTextColor = StagehandColors.TextPrimary,
                                    unfocusedTextColor = StagehandColors.TextPrimary
                                ),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) }
                            )

                            ExposedDropdownMenu(
                                expanded = showCategoryDropdown,
                                onDismissRequest = { showCategoryDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Uncategorized") },
                                    onClick = {
                                        viewModel.onEvent(UrlDetailEvent.SelectCategory(null))
                                        showCategoryDropdown = false
                                    }
                                )
                                state.categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.name) },
                                        onClick = {
                                            viewModel.onEvent(UrlDetailEvent.SelectCategory(category.id))
                                            showCategoryDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Divider(color = StagehandColors.BorderColor)

                        // Status Radio Group
                        Text(
                            text = stringResource(R.string.status_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = StagehandColors.TextSecondary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.selectedStatus == null,
                                    onClick = { viewModel.onEvent(UrlDetailEvent.SelectStatus(null)) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = StagehandColors.AccentColor
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.status_none),
                                    color = StagehandColors.TextPrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.selectedStatus == UrlStatus.ON_SHOW,
                                    onClick = { viewModel.onEvent(UrlDetailEvent.SelectStatus(UrlStatus.ON_SHOW)) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = StagehandColors.OnShowGreen
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.on_show),
                                    color = StagehandColors.TextPrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.selectedStatus == UrlStatus.DUMP,
                                    onClick = { viewModel.onEvent(UrlDetailEvent.SelectStatus(UrlStatus.DUMP)) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = StagehandColors.DumpRed
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.status_dump),
                                    color = StagehandColors.TextPrimary
                                )
                            }
                        }

                        // Save Button
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
}
