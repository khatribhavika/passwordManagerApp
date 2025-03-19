package com.example.myapplication.uidesign

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.PasswordDatabaseHelper
import com.example.myapplication.data.PasswordEntity

@Composable
fun AddEditPasswordScreen(
    dbHelper: PasswordDatabaseHelper,
    password: PasswordEntity? = null,
    onDone: () -> Unit
) {
    var accountType by remember { mutableStateOf(password?.accountType ?: "") }
    var username by remember { mutableStateOf(password?.username ?: "") }
    var encryptedPassword by remember { mutableStateOf(password?.encryptedPassword ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }

    /* Validation error messages */
    var accountTypeError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val isEditing = password != null
    val accountTypeFocusRequester = remember { FocusRequester() }
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        accountTypeFocusRequester.requestFocus()
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (isEditing) "Edit Account" else "Add New Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = accountType,
                onValueChange = {
                    accountType = it
                    if (it.isNotBlank()) accountTypeError = null
                },
                label = { Text("Account Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(accountTypeFocusRequester),
                isError = accountTypeError != null,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { usernameFocusRequester.requestFocus() }
                )
            )
            if (accountTypeError != null) {
                Text(
                    text = accountTypeError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    if (it.isNotBlank()) usernameError = null
                },
                label = { Text("Username / Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocusRequester),
                isError = usernameError != null,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                )
            )
            if (usernameError != null) {
                Text(
                    text = usernameError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = encryptedPassword,
                onValueChange = {
                    encryptedPassword = it
                    if (it.isNotBlank()) passwordError = null
                },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                isError = passwordError != null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color(0xFF0066FF),
                        modifier = Modifier
                            .clickable { passwordVisible = !passwordVisible }
                            .padding(8.dp)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        savePassword(
                            isEditing = isEditing,
                            dbHelper = dbHelper,
                            password = password,
                            accountType = accountType,
                            username = username,
                            encryptedPassword = encryptedPassword,
                            setAccountTypeError = { accountTypeError = it },
                            setUsernameError = { usernameError = it },
                            setPasswordError = { passwordError = it },
                            onDone = onDone
                        )
                    }
                )
            )

            if (passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    savePassword(
                        isEditing = isEditing,
                        dbHelper = dbHelper,
                        password = password,
                        accountType = accountType,
                        username = username,
                        encryptedPassword = encryptedPassword,
                        setAccountTypeError = { accountTypeError = it },
                        setUsernameError = { usernameError = it },
                        setPasswordError = { passwordError = it },
                        onDone = onDone
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = if (isEditing) "Save Changes" else "Add New Account",
                    color = Color.White
                )
            }
        }
    }
}

private fun savePassword(
    isEditing: Boolean,
    dbHelper: PasswordDatabaseHelper,
    password: PasswordEntity?,
    accountType: String,
    username: String,
    encryptedPassword: String,
    setAccountTypeError: (String?) -> Unit,
    setUsernameError: (String?) -> Unit,
    setPasswordError: (String?) -> Unit,
    onDone: () -> Unit
) {
    var isValid = true
    if (accountType.isBlank()) {
        setAccountTypeError("Account name cannot be empty")
        isValid = false
    }
    if (username.isBlank()) {
        setUsernameError("Username / Email cannot be empty")
        isValid = false
    }
    if (encryptedPassword.isBlank()) {
        setPasswordError("Password cannot be empty")
        isValid = false
    }

    if (!isValid) return

    if (isEditing) {
        dbHelper.updatePassword(
            PasswordEntity(
                id = password!!.id,
                accountType = accountType,
                username = username,
                encryptedPassword = encryptedPassword
            )
        )
    } else {
        dbHelper.insertPassword(
            PasswordEntity(
                accountType = accountType,
                username = username,
                encryptedPassword = encryptedPassword
            )
        )
    }

    dbHelper.logAllPasswords()
    onDone()
}