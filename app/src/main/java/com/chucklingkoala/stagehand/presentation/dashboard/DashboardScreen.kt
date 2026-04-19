package com.chucklingkoala.stagehand.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.domain.model.Episode
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.presentation.components.BulkActionBar
import com.chucklingkoala.stagehand.presentation.components.CategoryPickerSheet
import com.chucklingkoala.stagehand.presentation.components.EpisodePickerSheet
import com.chucklingkoala.stagehand.presentation.components.QuickSubmitSheet
import com.chucklingkoala.stagehand.presentation.components.UrlCard
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

// Which per-card / bulk picker sheet is active.
private sealed class ActiveSheet {
    data class CategoryForUrl(val urlId: Int, val currentId: Int?) : ActiveSheet()
    data class EpisodeForUrl(val urlId: Int, val currentId: Int?) : ActiveSheet()
    object CategoryForBulk : ActiveSheet()
    object EpisodeForBulk : ActiveSheet()
    object QuickSubmit : ActiveSheet()
    object DeleteBulkConfirm : ActiveSheet()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = get(),
    onNavigateToUrl: (Int) -> Unit,
    onNavigateToCategories: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var activeSheet by remember { mutableStateOf<ActiveSheet?>(null) }

    // Refresh when the screen resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(DashboardEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Infinite scroll
    LaunchedEffect(listState.canScrollForward) {
        if (!listState.canScrollForward && state.hasMore && !state.isLoadingMore) {
            viewModel.onEvent(DashboardEvent.LoadMore)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FilterDrawer(
                state = state,
                onSelectFilter = { filter ->
                    viewModel.onEvent(DashboardEvent.SelectFilter(filter))
                    scope.launch { drawerState.close() }
                },
                onManageCategories = {
                    scope.launch { drawerState.close() }
                    onNavigateToCategories()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = filterTitle(state.selectedFilter, state.categories, state.episodes),
                            color = StagehandColors.TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.open_drawer),
                                tint = StagehandColors.TextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.toggle_search),
                                tint = StagehandColors.TextPrimary
                            )
                        }
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
                                text = { Text(stringResource(R.string.menu_categories)) },
                                onClick = {
                                    showMenu = false
                                    onNavigateToCategories()
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = StagehandColors.BackgroundSecondary,
                        titleContentColor = StagehandColors.TextPrimary
                    )
                )
            },
            floatingActionButton = {
                if (!state.isSelectionMode) {
                    FloatingActionButton(
                        onClick = { activeSheet = ActiveSheet.QuickSubmit },
                        containerColor = StagehandColors.ButtonPrimary,
                        contentColor = Color.White
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.submit_url_title)
                        )
                    }
                }
            },
            bottomBar = {
                if (state.isSelectionMode) {
                    BulkActionBar(
                        selectedCount = state.selectedUrlIds.size,
                        onClear = { viewModel.onEvent(DashboardEvent.ClearSelection) },
                        onAssignCategory = { activeSheet = ActiveSheet.CategoryForBulk },
                        onAssignEpisode = { activeSheet = ActiveSheet.EpisodeForBulk },
                        onFlagOnShow = {
                            viewModel.onEvent(DashboardEvent.BulkFlag(UrlStatus.ON_SHOW))
                        },
                        onFlagDumped = {
                            viewModel.onEvent(DashboardEvent.BulkFlag(UrlStatus.DUMP))
                        },
                        onMarkCovered = {
                            viewModel.onEvent(DashboardEvent.BulkMarkCovered(true))
                        },
                        onUncover = {
                            viewModel.onEvent(DashboardEvent.BulkMarkCovered(false))
                        },
                        onDelete = { activeSheet = ActiveSheet.DeleteBulkConfirm }
                    )
                }
            },
            containerColor = StagehandColors.BackgroundPrimary
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (showSearchBar) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            viewModel.onEvent(DashboardEvent.Search(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_hint),
                                color = StagehandColors.TextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = StagehandColors.TextSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = StagehandColors.InputBackground,
                            unfocusedContainerColor = StagehandColors.InputBackground,
                            focusedTextColor = StagehandColors.TextPrimary,
                            unfocusedTextColor = StagehandColors.TextPrimary,
                            focusedBorderColor = StagehandColors.AccentColor,
                            unfocusedBorderColor = StagehandColors.BorderColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                when {
                    state.isRefreshing && state.urls.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = StagehandColors.AccentColor)
                        }
                    }
                    state.error != null && state.urls.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.error_loading_urls),
                                    color = StagehandColors.TextPrimary,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = state.error ?: "",
                                    color = StagehandColors.TextSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.onEvent(DashboardEvent.Refresh) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StagehandColors.AccentColor
                                    )
                                ) {
                                    Text(stringResource(R.string.retry))
                                }
                            }
                        }
                    }
                    state.urls.isEmpty() && !state.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (state.searchQuery.isNotBlank() ||
                                    state.selectedFilter != DashboardFilter.All
                                ) {
                                    stringResource(R.string.no_urls_match_filters)
                                } else {
                                    stringResource(R.string.no_urls_found)
                                },
                                color = StagehandColors.TextSecondary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    else -> {
                        PullToRefreshBox(
                            isRefreshing = state.isRefreshing,
                            onRefresh = { viewModel.onEvent(DashboardEvent.Refresh) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.urls, key = { it.id }) { url ->
                                    UrlCard(
                                        url = url,
                                        categories = state.categories,
                                        selectionMode = state.isSelectionMode,
                                        isSelected = state.selectedUrlIds.contains(url.id),
                                        onCardTap = {
                                            if (state.isSelectionMode) {
                                                viewModel.onEvent(
                                                    DashboardEvent.ToggleSelection(url.id)
                                                )
                                            } else {
                                                onNavigateToUrl(url.id)
                                            }
                                        },
                                        onLongPress = {
                                            viewModel.onEvent(
                                                DashboardEvent.ToggleSelection(url.id)
                                            )
                                        },
                                        onStatusChange = { newStatus ->
                                            viewModel.onEvent(
                                                DashboardEvent.ChangeStatus(url.id, newStatus)
                                            )
                                        },
                                        onCategoryClick = {
                                            activeSheet = ActiveSheet.CategoryForUrl(
                                                urlId = url.id,
                                                currentId = url.categoryId
                                            )
                                        },
                                        onEpisodeClick = {
                                            activeSheet = ActiveSheet.EpisodeForUrl(
                                                urlId = url.id,
                                                currentId = url.episodeId
                                            )
                                        },
                                        onCoveredToggle = { covered ->
                                            viewModel.onEvent(
                                                DashboardEvent.ToggleCovered(url.id, covered)
                                            )
                                        }
                                    )
                                }

                                if (state.isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                color = StagehandColors.AccentColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Active sheets / dialogs ---
    when (val sheet = activeSheet) {
        is ActiveSheet.CategoryForUrl -> CategoryPickerSheet(
            categories = state.categories,
            currentCategoryId = sheet.currentId,
            onPick = { newId ->
                viewModel.onEvent(DashboardEvent.ChangeCategory(sheet.urlId, newId))
                activeSheet = null
            },
            onDismiss = { activeSheet = null }
        )
        is ActiveSheet.EpisodeForUrl -> EpisodePickerSheet(
            episodes = state.episodes,
            currentEpisodeId = sheet.currentId,
            onPick = { newId ->
                viewModel.onEvent(DashboardEvent.ChangeEpisode(sheet.urlId, newId))
                activeSheet = null
            },
            onDismiss = { activeSheet = null }
        )
        ActiveSheet.CategoryForBulk -> CategoryPickerSheet(
            categories = state.categories,
            currentCategoryId = null,
            onPick = { newId ->
                viewModel.onEvent(DashboardEvent.BulkCategorize(newId))
                activeSheet = null
            },
            onDismiss = { activeSheet = null }
        )
        ActiveSheet.EpisodeForBulk -> EpisodePickerSheet(
            episodes = state.episodes,
            currentEpisodeId = null,
            onPick = { newId ->
                viewModel.onEvent(DashboardEvent.BulkAssignEpisode(newId))
                activeSheet = null
            },
            onDismiss = { activeSheet = null }
        )
        ActiveSheet.QuickSubmit -> QuickSubmitSheet(
            isSubmitting = state.isSubmitting,
            onSubmit = { url, title ->
                viewModel.onEvent(DashboardEvent.SubmitUrl(url, title))
                activeSheet = null
            },
            onDismiss = { activeSheet = null }
        )
        ActiveSheet.DeleteBulkConfirm -> AlertDialog(
            onDismissRequest = { activeSheet = null },
            title = { Text(stringResource(R.string.bulk_delete_confirm_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.bulk_delete_confirm_message,
                        state.selectedUrlIds.size
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(DashboardEvent.BulkDelete)
                    activeSheet = null
                }) {
                    Text(
                        stringResource(R.string.delete_confirm),
                        color = StagehandColors.ButtonDanger
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { activeSheet = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = StagehandColors.BackgroundSecondary
        )
        null -> Unit
    }
}

@Composable
private fun FilterDrawer(
    state: DashboardState,
    onSelectFilter: (DashboardFilter) -> Unit,
    onManageCategories: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = StagehandColors.BackgroundSecondary,
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.app_name),
                    color = StagehandColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                DrawerItem(
                    label = stringResource(R.string.filter_inbox),
                    selected = state.selectedFilter == DashboardFilter.Inbox,
                    onClick = { onSelectFilter(DashboardFilter.Inbox) }
                )
                DrawerItem(
                    label = stringResource(R.string.filter_all),
                    selected = state.selectedFilter == DashboardFilter.All,
                    onClick = { onSelectFilter(DashboardFilter.All) }
                )
                DrawerItem(
                    label = stringResource(R.string.status_on_show),
                    selected = (state.selectedFilter as? DashboardFilter.Status)?.status == UrlStatus.ON_SHOW,
                    onClick = { onSelectFilter(DashboardFilter.Status(UrlStatus.ON_SHOW)) }
                )
                DrawerItem(
                    label = stringResource(R.string.status_dumped),
                    selected = (state.selectedFilter as? DashboardFilter.Status)?.status == UrlStatus.DUMP,
                    onClick = { onSelectFilter(DashboardFilter.Status(UrlStatus.DUMP)) }
                )
                DrawerItem(
                    label = stringResource(R.string.filter_covered),
                    selected = state.selectedFilter == DashboardFilter.Covered,
                    onClick = { onSelectFilter(DashboardFilter.Covered) }
                )
                DrawerItem(
                    label = stringResource(R.string.filter_uncategorized),
                    selected = state.selectedFilter == DashboardFilter.Uncategorized,
                    onClick = { onSelectFilter(DashboardFilter.Uncategorized) }
                )
                Divider(color = StagehandColors.BorderColor, modifier = Modifier.padding(vertical = 8.dp))
            }

            if (state.categories.isNotEmpty()) {
                item {
                    DrawerSectionHeader(label = stringResource(R.string.section_categories))
                }
                items(state.categories, key = { "cat-${it.id}" }) { cat ->
                    CategoryDrawerItem(
                        category = cat,
                        selected = (state.selectedFilter as? DashboardFilter.Category)?.id == cat.id,
                        onClick = { onSelectFilter(DashboardFilter.Category(cat.id)) }
                    )
                }
                item {
                    DrawerItem(
                        label = stringResource(R.string.manage_categories),
                        selected = false,
                        onClick = onManageCategories
                    )
                    Divider(color = StagehandColors.BorderColor, modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            if (state.episodes.isNotEmpty()) {
                item {
                    DrawerSectionHeader(label = stringResource(R.string.section_episodes))
                }
                items(state.episodes, key = { "ep-${it.id}" }) { ep ->
                    EpisodeDrawerItem(
                        episode = ep,
                        selected = (state.selectedFilter as? DashboardFilter.Episode)?.id == ep.id,
                        onClick = { onSelectFilter(DashboardFilter.Episode(ep.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerSectionHeader(label: String) {
    Text(
        text = label,
        color = StagehandColors.TextSecondary,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun DrawerItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(text = label, color = StagehandColors.TextPrimary)
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = StagehandColors.AccentColor.copy(alpha = 0.25f)
        ),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun CategoryDrawerItem(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit
) {
    val swatchColor = try {
        Color(android.graphics.Color.parseColor(category.color))
    } catch (e: Exception) {
        Color.Gray
    }
    NavigationDrawerItem(
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(swatchColor)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = category.name,
                    color = StagehandColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (category.urlCount > 0) {
                    Text(
                        text = category.urlCount.toString(),
                        color = StagehandColors.TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = StagehandColors.AccentColor.copy(alpha = 0.25f)
        ),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun EpisodeDrawerItem(
    episode: Episode,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.episode_label, episode.episodeNumber),
                    color = StagehandColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (episode.urlCount > 0) {
                    Text(
                        text = episode.urlCount.toString(),
                        color = StagehandColors.TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = StagehandColors.AccentColor.copy(alpha = 0.25f)
        ),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun filterTitle(
    filter: DashboardFilter,
    categories: List<Category>,
    episodes: List<Episode>
): String = when (filter) {
    DashboardFilter.All -> stringResource(R.string.dashboard_title)
    DashboardFilter.Inbox -> stringResource(R.string.filter_inbox)
    DashboardFilter.Uncategorized -> stringResource(R.string.filter_uncategorized)
    DashboardFilter.Covered -> stringResource(R.string.filter_covered)
    is DashboardFilter.Status -> when (filter.status) {
        UrlStatus.ON_SHOW -> stringResource(R.string.status_on_show)
        UrlStatus.DUMP -> stringResource(R.string.status_dumped)
    }
    is DashboardFilter.Category ->
        categories.firstOrNull { it.id == filter.id }?.name
            ?: stringResource(R.string.dashboard_title)
    is DashboardFilter.Episode ->
        episodes.firstOrNull { it.id == filter.id }?.let {
            stringResource(R.string.episode_label, it.episodeNumber)
        } ?: stringResource(R.string.dashboard_title)
}
