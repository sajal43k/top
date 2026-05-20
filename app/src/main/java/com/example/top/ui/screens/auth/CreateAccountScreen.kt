package com.example.top.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.top.data.model.UserProfile
import com.example.top.ui.components.AuthCard
import com.example.top.ui.components.DarkGradientBackground
import com.example.top.ui.components.InlineLink
import com.example.top.ui.components.PasswordTextField
import com.example.top.ui.components.PrimaryActionButton
import com.example.top.ui.components.ScreenTitle
import com.example.top.ui.components.TopScoreTextField
import com.example.top.ui.viewmodel.AuthViewModel

@Composable
fun CreateAccountScreen(
    viewModel: AuthViewModel,
    onAccountCreated: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var firstPet by remember { mutableStateOf("") }
    var hasNoPet by remember { mutableStateOf(false) }
    var firstSchool by remember { mutableStateOf("") }
    var firstFriend by remember { mutableStateOf("") }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    DarkGradientBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ScreenTitle("Create account", "Add profile details now. Duplicate-name suggestions will come with Firebase search.")
            AuthCard {
                TopScoreTextField(name, { name = it }, "Full name")
                TopScoreTextField(email, { email = it }, "Email")
                TopScoreTextField(phone, { phone = it }, "Phone number")
                TopScoreTextField(profession, { profession = it }, "Profession (student, teacher, etc.)")
                TopScoreTextField(age, { age = it }, "Age")
                PasswordTextField(password, { password = it }, "Set password", showPassword, { showPassword = !showPassword })
                PasswordTextField(confirmPassword, { confirmPassword = it }, "Confirm password", showConfirmPassword, { showConfirmPassword = !showConfirmPassword })
                Text("Forgot-password questions")
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasNoPet, onCheckedChange = { hasNoPet = it })
                    Text("No pet")
                }
                if (!hasNoPet) TopScoreTextField(firstPet, { firstPet = it }, "First pet name")
                TopScoreTextField(firstSchool, { firstSchool = it }, "First school name")
                TopScoreTextField(firstFriend, { firstFriend = it }, "First friend name")
                PrimaryActionButton("Create account", uiState.isLoading) {
                    viewModel.createAccount(
                        profile = UserProfile(
                            name = name,
                            phoneNumber = phone,
                            profession = profession,
                            age = age,
                            firstPetName = firstPet,
                            hasNoPet = hasNoPet,
                            firstSchoolName = firstSchool,
                            firstFriendName = firstFriend
                        ),
                        
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        profileImageUri = null,
                        onSuccess = onAccountCreated
                    )
                }
                InlineLink("Already have account? Login", onBackToLogin)
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}
