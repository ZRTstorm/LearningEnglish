package com.example.learningenglish.ui.recommendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.viewmodel.FilterOption
import com.example.learningenglish.viewmodel.LearningViewModel
import com.example.learningenglish.viewmodel.SortOption
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    var selectedSortOption by remember { mutableStateOf(SortOption.TITLE) }
    var selectedFilterOption by remember { mutableStateOf(FilterOption.ALL) }

    LaunchedEffect(Unit) {
        viewModel.loadLibrary()
    }

    val libraryItems = viewModel.userLibrary.collectAsState().value
    val sortOption by viewModel.sortOption.collectAsState()
    val filterOption by viewModel.filterOption.collectAsState()

    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userPrefs.getUserId().collectLatest { userId ->
            if (userId != null) {
                viewModel.loadLibraryForUser(userId)
            }
        }
    }

    val filteredAndSortedItems = libraryItems
        .filter { filterLibraryItems(it, filterOption) }
        .sortedWith(getComparatorForSortOption(sortOption))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 라이브러리") },
                actions = {
                    SortOptionDropdownMenu(
                        items = SortOption.values().toList(),
                        selected = sortOption,
                        onItemSelected = { viewModel.setSortOption(it) }
                    )
                    FilterOptionDropdownMenu(
                        items = FilterOption.values().toList(),
                        selected = filterOption,
                        onItemSelected = { viewModel.setFilterOption(it) }
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Button(
                onClick = { navController.navigate("allvideos") },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(12.dp)
            ) {
                Text("전체 영상 보기")
            }

            if (libraryItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("등록된 콘텐츠가 없습니다.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(filteredAndSortedItems) { content ->
                        LibraryItemCard(content) {
                            navController.navigate("uploadresult/${content.contentType}/${0}/${30}") {
                                launchSingleTop = true
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SortOptionDropdownMenu(
    items: List<SortOption>,
    selected: SortOption,
    onItemSelected: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(Icons.Default.MoreVert, contentDescription = "More options")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { enumValue ->
            DropdownMenuItem(
                onClick = {
                    onItemSelected(enumValue)
                    expanded = false
                },
                interactionSource = interactionSource,
                text = { Text(enumValue.name) }
            )
        }
    }
}

@Composable
fun FilterOptionDropdownMenu(
    items: List<FilterOption>,
    selected: FilterOption,
    onItemSelected: (FilterOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(Icons.Default.MoreVert, contentDescription = "More options")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { enumValue ->
            DropdownMenuItem(
                onClick = {
                    onItemSelected(enumValue)
                    expanded = false
                },
                interactionSource = interactionSource,
                text = { Text(enumValue.name) }
            )
        }
    }
}

fun filterLibraryItems(item: UserLibraryContent, filterOption: FilterOption): Boolean {
    return when (filterOption) {
        FilterOption.ALL -> true
        FilterOption.VIDEO_ONLY -> item.contentType == "VIDEO"
        FilterOption.IMAGE_ONLY -> item.contentType == "IMAGE"
    }
}

fun getComparatorForSortOption(sortOption: SortOption): Comparator<UserLibraryContent> {
    return when (sortOption) {
        SortOption.TITLE -> compareBy { it.title }
        SortOption.CATEGORY -> compareBy { it.category }
        SortOption.UPLOADED_AT -> compareBy { it.uploadedAt }
    }
}

@Composable
fun LibraryItemCard(content: UserLibraryContent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = content.title ?: "", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("유형: ${content.contentType}", style = MaterialTheme.typography.bodySmall)
            Text("카테고리: ${content.category}", style = MaterialTheme.typography.bodySmall)
            Text("난이도: ${content.difficultyLevel}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
