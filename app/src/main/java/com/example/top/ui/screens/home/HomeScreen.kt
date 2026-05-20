package com.example.top.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.top.data.model.GroupSummary
import com.example.top.ui.components.DarkGradientBackground
import com.example.top.ui.components.InternetBanner
import com.example.top.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onCreateGroup: () -> Unit, authViewModel: com.example.top.ui.viewmodel.AuthViewModel, viewModel: HomeViewModel = HomeViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showCreateGroup by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }

    LaunchedEffect(authUiState.currentUser?.uid, authUiState.currentUser?.name) {
        viewModel.setUserName(authUiState.currentUser?.name.orEmpty())
        val uid = authUiState.currentUser?.uid.orEmpty()
        if (uid.isNotBlank()) {
            viewModel.start(uid)
        }
        } else {
            viewModel.startForCurrentUser()
        }
    LaunchedEffect(authUiState.currentUser?.name) {
        viewModel.setUserName(authUiState.currentUser?.name.orEmpty())
        authUiState.currentUser?.uid?.takeIf { it.isNotBlank() }?.let { viewModel.start(it) }
    }

    LaunchedEffect(authUiState.currentUser?.name) {
        viewModel.setUserName(authUiState.currentUser?.name.orEmpty())
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color(0xFF111827)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.close() } }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to home", tint = Color.White)
                    }
                    Text("Top Score", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                }
                NavigationDrawerItem(label = { Text("Edit profile") }, selected = false, onClick = {})
                NavigationDrawerItem(label = { Text("Change language") }, selected = false, onClick = {})
                NavigationDrawerItem(label = { Text("Create group") }, selected = false, onClick = { showCreateGroup = true })
                NavigationDrawerItem(label = { Text("Give feedback") }, selected = false, onClick = {})
                NavigationDrawerItem(label = { Text("Logout") }, selected = false, onClick = { authViewModel.logout() })
            }
        }
    ) {
        DarkGradientBackground {
            Scaffold(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    ExtendedFloatingActionButton(onClick = { showCreateGroup = true }, icon = { Icon(Icons.Default.Add, null) }, text = { Text("Create group") })
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        InternetBanner(isVisible = !uiState.isInternetAvailable)
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, contentDescription = "Open profile menu", tint = Color.White) }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Home", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text("Hello, ${uiState.userName}", color = Color(0xFFB6C2D4))
                            }
                            Row {
                                IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) }
                                IconButton(onClick = { authViewModel.logout() }) { Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White) }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Search users or groups by name/phone") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            singleLine = true
                        )
                    }
                    if (uiState.isLoading) item { EmptyGroupMessage("Loading groups...") }
                    item { SectionTitle("Groups created by you") }
                    if (uiState.createdGroups.isEmpty()) {
                        item { EmptyGroupMessage("No group created yet.") }
                    } else {
                        items(uiState.createdGroups) { group -> GroupCard(group) }
                    }
                    item { SectionTitle("Groups you are in") }
                    if (uiState.joinedGroups.isEmpty()) {
                        item { EmptyGroupMessage("No group joined yet.") }
                    } else {
                        items(uiState.joinedGroups) { group -> GroupCard(group) }
                    }
                }
            }
        }
    }
    if (showCreateGroup) {
        AlertDialog(
            onDismissRequest = { showCreateGroup = false },
            title = { Text("Create Group") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = groupName, onValueChange = { groupName = it }, label = { Text("Group name") })
                    OutlinedTextField(value = groupDescription, onValueChange = { groupDescription = it }, label = { Text("Description") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createGroup(groupName, groupDescription)
                    showCreateGroup = false
                    groupName = ""
                    groupDescription = ""
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showCreateGroup = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
}

@Composable
private fun EmptyGroupMessage(text: String) {
    Text(text, color = Color(0xFFB6C2D4), modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun GroupCard(group: GroupSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE61C2433))
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.clip(CircleShape).background(Color(0xFF2563EB)).padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Text(group.name.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(group.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Created by ${group.ownerName} • ${group.memberCount} members", color = Color(0xFFB6C2D4))
            }
        }
    }
}
