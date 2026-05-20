package com.example.top.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.top.ui.components.AuthCard
import com.example.top.ui.components.DarkGradientBackground
import com.example.top.ui.components.PrimaryActionButton
import com.example.top.ui.components.TopScoreTextField
import com.example.top.ui.viewmodel.HomeViewModel

@Composable
fun CreateGroupScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    DarkGradientBackground {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Create group")
            AuthCard {
                TopScoreTextField(value = groupName, onValueChange = { groupName = it }, label = "Group name")
                TopScoreTextField(value = description, onValueChange = { description = it }, label = "Description", singleLine = false)
                PrimaryActionButton(text = "Save group") {
                    viewModel.createGroup(groupName, description)
                    onBack()
                }
            }
        }
    }
}
