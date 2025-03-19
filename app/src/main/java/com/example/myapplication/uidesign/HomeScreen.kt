package com.example.myapplication.uidesign
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material.icons.filled.KeyboardArrowRight
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.FloatingActionButton
    import androidx.compose.material3.Icon
    import androidx.compose.material3.ModalBottomSheet
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.rememberModalBottomSheetState
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import com.example.myapplication.data.PasswordDatabaseHelper
    import com.example.myapplication.data.PasswordEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, dbHelper: PasswordDatabaseHelper) {
    var passwords by remember { mutableStateOf(emptyList<PasswordEntity>()) }

    var selectedPassword by remember { mutableStateOf<PasswordEntity?>(null) }

    // Bottom sheet visibility states
    var showDetailsSheet by remember { mutableStateOf(false) }
    var showAddEditSheet by remember { mutableStateOf(false) }

    val detailsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val addEditSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    fun refreshPasswords() {
        passwords = dbHelper.getAllPasswords()
    }

    LaunchedEffect(Unit) {
        refreshPasswords()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedPassword = null
                    showAddEditSheet = true
                },
                containerColor = Color(0xFF0066FF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Password", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
        ) {
            Text(
                text = "Password Manager",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (passwords.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No passwords added yet.", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(passwords) { password ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    selectedPassword = password
                                    showDetailsSheet = true
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = password.accountType,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = "View Details",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Details Bottom Sheet
        if (showDetailsSheet && selectedPassword != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDetailsSheet = false
                    selectedPassword = null
                    refreshPasswords()
                },
                sheetState = detailsSheetState,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                PasswordDetailsScreen(
                    password = selectedPassword!!,
                    onEdit = {
                        showDetailsSheet = false
                        showAddEditSheet = true
                    },
                    onDelete = {
                        dbHelper.deletePassword(selectedPassword!!.id)
                        refreshPasswords()
                        showDetailsSheet = false
                        selectedPassword = null
                    }
                )
            }
        }

        // Add/Edit Bottom Sheet
        if (showAddEditSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAddEditSheet = false
                    selectedPassword = null
                    refreshPasswords()
                },
                sheetState = addEditSheetState,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                AddEditPasswordScreen(
                    dbHelper = dbHelper,
                    password = selectedPassword,
                    onDone = {
                        showAddEditSheet = false
                        selectedPassword = null
                        refreshPasswords()
                    }
                )
            }
        }
    }
}


