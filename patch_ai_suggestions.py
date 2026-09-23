with open('app/src/main/java/com/example/ui/dashboard/AiAssistantScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('AiSuggestionCard("What is my current attendance?", onClick = { viewModel.sendMessage("What is my current attendance?") })', 'AiSuggestionCard("Check my attendance", onClick = { viewModel.sendMessage("Check my attendance") })')
content = content.replace('AiSuggestionCard("How many classes can I skip?", onClick = { viewModel.sendMessage("How many classes can I skip?") })', 'AiSuggestionCard("Attendance risk", onClick = { viewModel.sendMessage("Attendance risk") })')
content = content.replace('AiSuggestionCard("Show my external marks.", onClick = { viewModel.sendMessage("Show my external marks.") })', 'AiSuggestionCard("Calculate required classes", onClick = { viewModel.sendMessage("Calculate required classes") })')
content = content.replace('AiSuggestionCard("Which subject needs focus?", onClick = { viewModel.sendMessage("Which subject needs focus?") })', 'AiSuggestionCard("Analyze my marks", onClick = { viewModel.sendMessage("Analyze my marks") })\n                            AiSuggestionCard("Study plan", onClick = { viewModel.sendMessage("Study plan") })\n                            AiSuggestionCard("Ask about my materials", onClick = { viewModel.sendMessage("Ask about my materials") })')

with open('app/src/main/java/com/example/ui/dashboard/AiAssistantScreen.kt', 'w') as f:
    f.write(content)
