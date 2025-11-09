package com.chucklingkoala.stagehand.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.presentation.components.UrlCard
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import org.koin.androidx.compose.get

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

    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Refresh when returning to this screen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(DashboardEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Detect when we're near the bottom for infinite scroll
    LaunchedEffect(listState.canScrollForward) {
        if (!listState.canScrollForward && state.hasMore && !state.isLoadingMore) {
            viewModel.onEvent(DashboardEvent.LoadMore)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        color = StagehandColors.TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Toggle Search",
                            tint = StagehandColors.TextPrimary
                        )
                    }
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
        containerColor = StagehandColors.BackgroundPrimary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar (collapsible)
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
                            contentDescription = "Search",
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

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All filter
                FilterChip(
                    selected = state.selectedCategoryId == null &&
                            state.selectedStatus == null &&
                            !state.showUncategorized,
                    onClick = { viewModel.onEvent(DashboardEvent.ClearFilters) },
                    label = { Text(stringResource(R.string.filter_all)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = StagehandColors.CardBackground,
                        selectedContainerColor = StagehandColors.AccentColor,
                        labelColor = StagehandColors.TextPrimary,
                        selectedLabelColor = StagehandColors.TextPrimary
                    )
                )

                // On Show filter
                FilterChip(
                    selected = state.selectedStatus == UrlStatus.ON_SHOW,
                    onClick = {
                        viewModel.onEvent(
                            DashboardEvent.FilterByStatus(
                                if (state.selectedStatus == UrlStatus.ON_SHOW) null else UrlStatus.ON_SHOW
                            )
                        )
                    },
                    label = { Text(stringResource(R.string.filter_on_show)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = StagehandColors.CardBackground,
                        selectedContainerColor = StagehandColors.OnShowGreen,
                        labelColor = StagehandColors.TextPrimary,
                        selectedLabelColor = StagehandColors.BackgroundPrimary
                    )
                )

                // Dump filter
                FilterChip(
                    selected = state.selectedStatus == UrlStatus.DUMP,
                    onClick = {
                        viewModel.onEvent(
                            DashboardEvent.FilterByStatus(
                                if (state.selectedStatus == UrlStatus.DUMP) null else UrlStatus.DUMP
                            )
                        )
                    },
                    label = { Text(stringResource(R.string.filter_dump)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = StagehandColors.CardBackground,
                        selectedContainerColor = StagehandColors.DumpRed,
                        labelColor = StagehandColors.TextPrimary,
                        selectedLabelColor = StagehandColors.TextPrimary
                    )
                )

                // Uncategorized filter
                FilterChip(
                    selected = state.showUncategorized,
                    onClick = { viewModel.onEvent(DashboardEvent.FilterUncategorized) },
                    label = { Text(stringResource(R.string.filter_uncategorized)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = StagehandColors.CardBackground,
                        selectedContainerColor = StagehandColors.AccentColor,
                        labelColor = StagehandColors.TextPrimary,
                        selectedLabelColor = StagehandColors.TextPrimary
                    )
                )

                // Category filters
                state.categories.forEach { category ->
                    FilterChip(
                        selected = state.selectedCategoryId == category.id,
                        onClick = {
                            viewModel.onEvent(
                                DashboardEvent.FilterByCategory(
                                    if (state.selectedCategoryId == category.id) null else category.id
                                )
                            )
                        },
                        label = { Text(category.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = StagehandColors.CardBackground,
                            selectedContainerColor = try {
                                androidx.compose.ui.graphics.Color(
                                    android.graphics.Color.parseColor(category.color)
                                )
                            } catch (e: Exception) {
                                StagehandColors.AccentColor
                            },
                            labelColor = StagehandColors.TextPrimary,
                            selectedLabelColor = StagehandColors.TextPrimary
                        )
                    )
                }
            }

            // URL List
            if (state.isRefreshing && state.urls.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StagehandColors.AccentColor)
                }
            } else if (state.error != null && state.urls.isEmpty()) {
                // Show error message if there's an error and no URLs loaded
                val errorMessage = state.error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error loading URLs",
                            color = StagehandColors.TextPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = StagehandColors.TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onEvent(DashboardEvent.Refresh) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StagehandColors.AccentColor
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else if (state.urls.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.searchQuery.isNotBlank() ||
                            state.selectedCategoryId != null ||
                            state.selectedStatus != null ||
                            state.showUncategorized
                        ) {
                            stringResource(R.string.no_urls_match_filters)
                        } else {
                            stringResource(R.string.no_urls_found)
                        },
                        color = StagehandColors.TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
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
                                onCardClick = onNavigateToUrl,
                                onStatusToggle = { id, status ->
                                    viewModel.onEvent(DashboardEvent.ToggleUrlStatus(id, status))
                                },
                                onCategorizeClick = { id ->
                                    // TODO: Show category picker dialog
                                    onNavigateToUrl(id)
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

            // Error Snackbar
            state.error?.let { error ->
                LaunchedEffect(error) {
                    // Show error to user
                    println("Error: $error")
                }
            }
        }
    }
}
