# Lessons Learned

## GitHub Copilot Best Practices

### Error Handling Standards
```kotlin
// ❌ BAD: Missing error handling in critical functions
suspend fun initialize() {
    // Load models without try-catch
    modelsLoaded = true
}

// ✅ GOOD: Comprehensive error handling
suspend fun initialize() {
    if (isInitialized) return
    try {
        // Model loading operations
        modelsLoaded = true
        isInitialized = true
    } catch (e: Exception) {
        isInitialized = false
        throw RuntimeException("Failed to initialize: ${e.message}", e)
    }
}
```

### Placeholder Implementation Patterns
```kotlin
// ❌ BAD: Silent empty returns that hide incomplete features
suspend fun generatePlaylist(): List<String> {
    return emptyList() // Implementation pending
}

// ✅ GOOD: Explicit not-implemented errors
suspend fun generatePlaylist(): List<String> {
    Log.w(TAG, "generatePlaylist is not yet implemented")
    return emptyList()
}
```

### State vs Parameter Validation
```kotlin
// ❌ BAD: Using require() for state validation
suspend fun analyzeAudio(buffer: FloatArray): Result {
    require(isInitialized) { "Not initialized" } // Wrong - this is state, not parameter
}

// ✅ GOOD: Use check() for state, require() for parameters
suspend fun analyzeAudio(buffer: FloatArray): Result {
    check(isInitialized) { "Neural processor not initialized" } // State validation
    require(buffer.isNotEmpty()) { "Audio buffer cannot be empty" } // Parameter validation
}
```

### Silent Failures and Logging
```kotlin
// ❌ BAD: Silent fallback that masks issues
fun processWithModel(data: Data): Result {
    if (!modelLoaded) return data // Silent failure
}

// ✅ GOOD: Explicit logging for fallback behavior
fun processWithModel(data: Data): Result {
    if (!modelLoaded) {
        Log.w(TAG, "Model not loaded. Skipping processing and returning input unchanged.")
        return data
    }
}
```

### Multi-line Edit Quality Control
```kotlin
// ❌ BAD: Duplicate lines from careless multi-line edits
private fun processAudio() {
    // Extract audio features
    // Extract audio features  // <-- Duplicate comment
    return processedData
}

// ✅ GOOD: Clean, reviewed multi-line edits
private fun processAudio() {
    // Extract audio features and process with ML models
    return processedData
}
```

**Prevention strategies:**
- Always review the final diff after multi-line edits
- Use specific search patterns to find duplicates: `grep -n "^\s*//.*" file.kt | sort | uniq -d`
- When using MultiEdit tool, verify each edit individually
- Copilot reviews catch these but prevention is better

### Copilot Version Awareness Standards

**Version Lag Scenarios:**
- Copilot reviews are based on specific commit snapshots, not real-time code
- Issues may reference earlier versions that have already been fixed
- Always verify current code state before applying suggested fixes
- Mark todos as completed if issues are already resolved
- Still perform proactive searches for similar patterns across codebase

**Workflow for Copilot Issues:**
1. **Verify current state first**: Check actual code before applying suggestions
2. **Mark completed if resolved**: If already fixed, mark todo as completed immediately  
3. **Search for similar patterns**: Even if specific issue is resolved, look for similar patterns elsewhere
4. **Update documentation**: Document lessons learned for future reference
5. **Commit comprehensive fixes**: Include all related improvements in single commit

## Build Artifacts and Repository Management

### APK Management Standards
```bash
# ❌ NEVER commit build artifacts to GitHub
git add app/build/outputs/apk/debug/app-debug.apk  # 109MB file - too large!
git add app/build/intermediates/                   # Thousands of generated files

# ✅ ALWAYS exclude build artifacts in .gitignore
*.apk
*.aab
build/
.gradle/
```

**Why APK files should NEVER be committed:**
- **Size**: APK files can be 50-100MB+, exceeding GitHub's limits
- **Regeneratable**: Build artifacts can always be recreated from source
- **Repository bloat**: Each APK commit adds massive size to git history
- **Platform-specific**: Not useful for other developers or CI/CD
- **Frequent changes**: Every build creates a different binary

**Proper artifact management:**
- Use `.gitignore` to exclude all build artifacts
- Store test APKs in `/apk-test-packages` directory following naming conventions (excluded from git)
- Store development assets in `/subc0der` directory (excluded from git)
- Follow APK naming conventions: `ftl-player-v{VERSION}-{SEQUENCE}-{DESCRIPTION}.apk`
- Use GitHub Releases or CI/CD for distributing builds
- Keep repository focused on source code only

## Development Workflow Lessons

### Task Management
- **Use TodoWrite proactively** for complex multi-step tasks
- **Mark todos completed immediately** after finishing each task
- **Exactly ONE task in_progress** at any time
- **Break complex tasks** into smaller, manageable steps

### Code Review Integration
- **Systematic Copilot resolution**: Check PR → Fix issues → Search patterns → Update docs → Commit
- **Proactive pattern fixing**: Don't just fix the specific issue, find similar patterns
- **Documentation updates**: Always update lessons learned and best practices
- **Quality over speed**: Better to fix comprehensively once than repeatedly

### Context Management
- **Provide complete context** to Claude about current state
- **Reference existing standards** and lessons learned
- **Update documentation** as patterns evolve
- **Maintain clean separation** between different concern areas