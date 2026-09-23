with open('app/src/main/java/com/example/ui/dashboard/AiAssistantScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'fun AiAssistantScreen(onBack: () -> Unit = {}) {',
    'import androidx.compose.runtime.collectAsState\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.setValue\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember\n\n@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun AiAssistantScreen(viewModel: AiAssistantViewModel, onBack: () -> Unit = {}) {'
)
content = content.replace(
    '@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nimport androidx.compose.runtime.collectAsState',
    'import androidx.compose.runtime.collectAsState'
)

# Replace the layout
old_chat = '''            // Chat Area
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Crescent AI", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("Your Academic Assistant", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AiSuggestionCard("What is my current attendance?")
                    AiSuggestionCard("How many classes can I skip?")
                    AiSuggestionCard("Show my external marks.")
                    AiSuggestionCard("Which subject needs focus?")
                }
            }'''

new_chat = '''            // Chat Area
            val messages by viewModel.messages.collectAsState()
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                if (messages.size <= 1) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Crescent AI", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text("Your Academic Assistant", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(48.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AiSuggestionCard("What is my current attendance?", onClick = { viewModel.sendMessage("What is my current attendance?") })
                            AiSuggestionCard("How many classes can I skip?", onClick = { viewModel.sendMessage("How many classes can I skip?") })
                            AiSuggestionCard("Show my external marks.", onClick = { viewModel.sendMessage("Show my external marks.") })
                            AiSuggestionCard("Which subject needs focus?", onClick = { viewModel.sendMessage("Which subject needs focus?") })
                        }
                    }
                } else {
                    messages.forEach { message ->
                        if (message.isUser) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Box(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)).padding(12.dp)
                                ) {
                                    Text(message.text, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        } else {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                Box(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)).padding(12.dp)
                                ) {
                                    if (message.isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text(message.text, color = if (message.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }'''
content = content.replace(old_chat, new_chat)

old_input = '''            // Input Area
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Ask anything...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FloatingActionButton(
                        onClick = { },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }'''

new_input = '''            // Input Area
            var inputText by remember { mutableStateOf("") }
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask anything...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FloatingActionButton(
                        onClick = {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }'''
content = content.replace(old_input, new_input)

content = content.replace(
    'fun AiSuggestionCard(text: String)',
    'fun AiSuggestionCard(text: String, onClick: () -> Unit = {})'
)
content = content.replace(
    'modifier = Modifier.fillMaxWidth(),',
    'modifier = Modifier.fillMaxWidth().androidx.compose.foundation.clickable { onClick() },'
)

with open('app/src/main/java/com/example/ui/dashboard/AiAssistantScreen.kt', 'w') as f:
    f.write(content)
