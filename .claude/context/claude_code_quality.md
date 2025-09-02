# Claude Code Quality Standards - FTL Hi-Res Audio Player
## Coding Standards & Best Practices

---

## 🎯 Code Quality Standards

### Kotlin/Android Standards
- Follow Android architecture guidelines (MVVM, Clean Architecture)
- Use Jetpack Compose for UI development
- Implement proper dependency injection with Hilt
- Write comprehensive unit tests (80%+ coverage)
- Use coroutines for asynchronous operations

---

## 🔧 GitHub Copilot Best Practices

Based on our AI-assisted development experience, follow these patterns to avoid Copilot issues and ensure code quality:

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
    throw NotImplementedError("generatePlaylist is not yet implemented")
}
```

### Magic Number Constants
```kotlin
// ❌ BAD: Hardcoded magic numbers scattered in code
return FloatArray(128) // Feature vector size
return List(32) { 0.0f } // EQ bands

// ✅ GOOD: Named constants with clear purpose
companion object {
    private const val AUDIO_FEATURE_VECTOR_SIZE = 128
    private const val EQ_BANDS_COUNT = 32
}
return FloatArray(AUDIO_FEATURE_VECTOR_SIZE)
return List(EQ_BANDS_COUNT) { 0.0f }
```

### Input Validation Best Practices
```kotlin
// ❌ BAD: Missing parameter validation
suspend fun processAudio(buffer: FloatArray, sampleRate: Int) {
    // Direct processing without checks
}

// ✅ GOOD: Comprehensive parameter validation
suspend fun processAudio(buffer: FloatArray, sampleRate: Int) {
    require(buffer.isNotEmpty()) { "Audio buffer cannot be empty" }
    require(sampleRate > 0) { "Sample rate must be positive, got: $sampleRate" }
    // Processing logic
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
        println("Warning: Model not loaded. Skipping processing and returning input unchanged.")
        return data
    }
}
```

### TODO vs NotImplementedError
```kotlin
// ❌ BAD: NotImplementedError for incomplete features
suspend fun generatePlaylist(): List<String> {
    throw NotImplementedError("Feature not implemented")
}

// ❌ BAD: Vague TODO message
suspend fun generatePlaylist(): List<String> {
    TODO("generatePlaylist is not yet implemented")
}

// ✅ GOOD: Detailed TODO with implementation plan and timeline
suspend fun generatePlaylist(): List<String> {
    TODO("Implement neural collaborative filtering for smart playlist generation using TensorFlow Lite. Will analyze user listening history, current biometric context (heart rate, activity), and audio similarity vectors to generate contextually appropriate playlists. Planned implementation: Q1 2025.")
}
```

### Documentation Standards
```kotlin
// ❌ BAD: Minimal documentation
suspend fun analyze(data: FloatArray): Result

// ✅ GOOD: Comprehensive documentation with error cases
/**
 * Analyze audio content in real-time
 * @param audioBuffer Audio samples for analysis
 * @param sampleRate Sample rate of the audio  
 * @return AudioIntelligence analysis results
 * @throws IllegalArgumentException if parameters are invalid
 * @throws IllegalStateException if processor not initialized
 */
suspend fun analyzeAudio(audioBuffer: FloatArray, sampleRate: Int): AudioIntelligence
```

---

## 📝 Logging Best Practices

```kotlin
// ❌ BAD: Using println() for production logging
if (!modelsLoaded) {
    println("Warning: Models not loaded")
    return defaultValue
}

// ✅ GOOD: Use Android Log with proper tag and level
companion object {
    private const val TAG = "NeuralAudioProcessor"
}

if (!modelsLoaded) {
    Log.w(TAG, "Neural audio processor models not loaded. Skipping enhancement and returning original buffer.")
    return defaultValue
}
```

---

## 🎯 Public API Safety Standards

```kotlin
// ❌ BAD: TODO() in public API can crash the application
suspend fun generatePlaylist(seeds: List<String>): List<String> {
    TODO("Not implemented yet") // This will crash when called!
}

// ❌ BAD: Poor grammar in log messages
Log.w(TAG, "Models not loaded") // Should be "are not loaded"

// ✅ GOOD: Safe placeholder with logging and graceful fallback
suspend fun generatePlaylist(seeds: List<String>): List<String> {
    Log.w(TAG, "generatePlaylist is not yet implemented. Returning empty playlist. Planned implementation: Q1 2025.")
    return emptyList()
}

// ✅ GOOD: Proper grammar in log messages
Log.w(TAG, "Neural audio processor models are not loaded. Skipping enhancement.")
```

**Public API Safety Rules:**
- Never use `TODO()` or `NotImplementedError()` in public methods
- Always provide safe fallback return values (empty lists, null, default objects)
- Log warnings about incomplete functionality with implementation timeline
- Update KDoc to reflect actual behavior: `@return Empty list until implementation complete`
- Use proper grammar in user-facing log messages

---

## 📚 Documentation Accuracy Standards

```kotlin
// ❌ BAD: KDoc describing future behavior instead of current reality
/**
 * Generate smart playlist using neural collaborative filtering
 * @return List of recommended track IDs based on user preferences and context
 */
suspend fun generatePlaylist(): List<String> {
    Log.w(TAG, "Not implemented yet")
    return emptyList() // Behavior doesn't match documentation!
}

// ✅ GOOD: Concise KDoc focused on current behavior
/**
 * Generate smart playlist using neural collaborative filtering
 * @return Empty list (not yet implemented)
 */
suspend fun generatePlaylist(): List<String> {
    Log.w(TAG, "generatePlaylist is not yet implemented")
    return emptyList()
}
```

**Documentation Conciseness Rules:**
- @return should describe current behavior only, not future plans
- Avoid implementation timelines in KDoc (put in code comments if needed)
- Use consistent grammar: "is not yet implemented"
- Keep log messages concise and focused
- Reserve detailed explanations for code comments, not public documentation

---

## ⚠️ Copilot Version Awareness Standards

```kotlin
// ⚠️ SCENARIO: Copilot flags missing error handling in initialize()
// But checking current code shows it's already implemented properly

// What Copilot may reference (earlier commit):
suspend fun initialize() {
    if (isInitialized) return
    modelsLoaded = true    // ← No error handling
    isInitialized = true
}

// What your current code actually contains:
suspend fun initialize() {
    if (isInitialized) return
    try {
        // Load neural network models
        modelsLoaded = true
        isInitialized = true
    } catch (e: Exception) {
        isInitialized = false
        throw RuntimeException("Failed to initialize: ${e.message}", e)
    }
}
```

**Copilot Version Lag Rules:**
- **Verify current state first**: Always check your actual code before applying Copilot suggestions
- **Mark completed if resolved**: If the issue is already fixed, mark the todo as completed immediately  
- **Still search for patterns**: Even if the specific issue is resolved, search for similar patterns elsewhere
- **Update documentation**: Document lessons learned for future reference
- **Commit verification**: Copilot reviews are based on specific commit snapshots, not real-time code
- **Timeline awareness**: Reviews may be delayed by several commits, especially in active development

---

## 🗂️ Build Artifacts and Repository Management

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

---

## 🎯 Key Quality Principles

1. **Proper Validation**: Use `require()` for parameters, `check()` for state
2. **Explicit Behaviors**: Log warnings for fallback behaviors, use detailed `TODO()` for incomplete features  
3. **Named Constants**: Replace all magic numbers with descriptive constants
4. **Comprehensive Docs**: Document all parameters, return values, and exceptions
5. **Graceful Degradation**: Handle initialization and resource loading failures
6. **Clear Intent**: Make all behaviors explicit rather than silent
7. **Proactive Error Handling**: Always wrap critical operations in try-catch blocks
8. **Professional Logging**: Use Android Log with TAG constants, avoid println() in production
9. **Clear Placeholders**: Document temporary implementations and avoid misleading comments
10. **No Duplicate Lines**: Always review multi-line edits for accidentally duplicated content
11. **Consistent State Validation**: Always validate initialization before checking sub-states
12. **Safe Public APIs**: Never use TODO() in public methods that could crash the app
13. **Accurate Documentation**: KDoc must reflect current behavior, not future plans
14. **Concise Documentation**: Keep KDoc focused on behavior, avoid implementation timelines
15. **Copilot Version Awareness**: Verify current code state when addressing Copilot feedback

---

## 🔗 Quick Navigation

- **[Core Workflow](claude-core-workflow.md)** - Main development process
- **[Architecture Standards](claude-architecture-standards.md)** - Technical architecture
- **[Project Documentation](README.md)** - Complete project overview
- **[Lessons Learned](lessons-learned.md)** - Development insights

---

*These patterns ensure Copilot reviews pass and maintain professional code quality standards for the FTL Hi-Res Audio Player.*