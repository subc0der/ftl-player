# Claude.md - FTL Hi-Res Audio Player Documentation

## Project Overview

**Project Name:** FTL Hi-Res Audio Player  
**Type:** Android Music Player Application  
**Development Approach:** AI-Assisted Development with Claude  
**Primary Goal:** Create the world's most advanced audiophile music player

## Development Philosophy

This project follows an iterative, AI-assisted development methodology where each feature is designed, implemented, tested, and refined in collaboration with Claude AI. The approach emphasizes:

- **Feature-driven development** - One feature at a time
- **Continuous testing and iteration**
- **Performance-first architecture**
- **User experience excellence**
- **Technical innovation**

## Project Structure

```
ftl-hi-res-audio-player/
├── app/                          # Main Android application
│   ├── src/main/kotlin/          # Kotlin source code
│   ├── src/main/res/             # Android resources
│   └── src/test/                 # Unit tests
├── audio-engine/                 # Native audio processing
│   ├── cpp/                      # C++ audio engine
│   └── jni/                      # JNI bindings
├── ml-models/                    # Machine learning components
│   ├── tensorflow-lite/          # TensorFlow Lite models
│   └── training-data/            # ML training datasets
├── docs/                         # Project documentation
│   ├── architecture/             # System architecture docs
│   ├── api/                      # API documentation
│   └── user-guides/              # User documentation
├── scripts/                      # Build and automation scripts
├── tests/                        # Integration and UI tests
└── assets/                       # Design assets and resources
```

## Claude Interaction Guidelines

### When to Use Claude Desktop
- **Project planning and architecture** discussions
- **Complex problem solving** that requires back-and-forth
- **Requirements clarification** and feature specification
- **Code review and optimization** discussions
- **Debugging complex issues** that need explanation

### When to Use Claude CLI
- **Active development** and code implementation
- **File creation and modification**
- **Running tests and build processes**
- **Automated code generation**
- **Large codebase refactoring**

### Context Management

#### Project Context Setup
Always provide Claude with:
1. **Current milestone** and feature being developed
2. **Recent changes** made to the codebase
3. **Specific requirements** for the current task
4. **Performance constraints** and technical limitations
5. **Integration points** with existing features

#### File Organization for Claude
- Keep related files in logical directories
- Use descriptive naming conventions
- Maintain clear separation between layers (UI, business logic, data)
- Document complex algorithms inline
- Keep configuration in easily accessible files

## Development Workflow

### Feature Development Cycle

1. **Planning Phase** (Claude Desktop)
   - Define feature requirements
   - Design technical approach
   - Identify dependencies and risks
   - Create implementation plan

2. **Implementation Phase** (Claude CLI)
   - Set up development environment
   - Implement core functionality
   - Write unit tests
   - Create initial UI components

3. **Testing Phase** (Claude CLI/Desktop)
   - Run automated tests
   - Perform manual testing
   - Identify and fix bugs
   - Performance optimization

4. **Integration Phase** (Claude CLI)
   - Integrate with existing features
   - Update documentation
   - Commit code changes
   - Prepare for next feature

### Code Quality Standards

#### Kotlin/Android Standards
- Follow Android architecture guidelines (MVVM, Clean Architecture)
- Use Jetpack Compose for UI development
- Implement proper dependency injection with Hilt
- Write comprehensive unit tests (80%+ coverage)
- Use coroutines for asynchronous operations

#### Performance Standards
- Audio latency: <50ms total
- UI responsiveness: 60fps minimum, 120fps target
- Memory usage: <200MB for UI, <50MB per audio stream
- Battery efficiency: <10% drain per hour during playback
- CPU usage: <15% during normal operation

#### Security Standards
- Encrypt sensitive user data
- Use secure communication protocols
- Implement proper input validation
- Follow Android security best practices
- Protect against reverse engineering

## Technical Architecture

### Core Components

#### Audio Engine
- **Native C++ processing** for maximum performance
- **Multi-threaded architecture** for real-time processing
- **Custom DSP algorithms** for EQ and effects
- **Format support** for all audiophile formats
- **Hardware integration** with external DACs

#### User Interface
- **Jetpack Compose** for modern, reactive UI
- **Custom animations** with 120fps target
- **Cyberpunk aesthetic** matching project theme
- **Responsive design** for all screen sizes
- **Accessibility support** for all users

#### Machine Learning
- **TensorFlow Lite** for on-device inference
- **Audio analysis models** for intelligent features
- **User behavior learning** for personalization
- **Privacy-first approach** with local processing
- **Continuous model improvement**

### Data Architecture

#### Local Storage
```
Room Database:
├── music_library/          # Song metadata and file paths
├── playlists/             # User-created playlists
├── eq_presets/           # Equalizer configurations
├── user_preferences/     # App settings and preferences
├── listening_history/    # Play counts and timestamps
└── audio_analysis/       # Cached frequency analysis data
```

#### Cloud Storage (Optional)
- **Encrypted user profiles** with preference backup
- **Cross-device synchronization** for playlists and settings
- **Anonymous usage analytics** for product improvement
- **Secure API communication** with backend services

## Testing Strategy

### Unit Testing
- **Coverage target:** 80%+ for all Kotlin code
- **Testing framework:** JUnit 5, Mockito, Truth
- **Audio testing:** Custom test harness for audio quality
- **Performance testing:** Automated benchmarks

### Integration Testing
- **UI testing:** Espresso for Android UI components
- **Audio pipeline testing:** End-to-end audio processing tests
- **Cross-feature testing:** Ensure features work together
- **Device compatibility:** Test on multiple Android versions

### User Testing
- **Alpha testing:** Internal testing with development team
- **Beta testing:** Closed testing with audiophile community
- **Usability testing:** User experience validation
- **Performance testing:** Real-world usage scenarios

## Deployment and Distribution

### Development Builds
- **Debug builds** for development testing
- **Automated CI/CD** pipeline for continuous integration
- **Internal distribution** via Firebase App Distribution
- **Performance monitoring** with crash reporting

### Release Builds
- **Google Play Console** for production distribution
- **Staged rollout** to minimize risk
- **A/B testing** for feature validation
- **User feedback collection** and analysis

### Version Management
- **Semantic versioning** (MAJOR.MINOR.PATCH)
- **Feature flags** for gradual feature rollout
- **Backward compatibility** maintenance
- **Database migration** strategies

## Success Metrics

### Technical Metrics
- **Audio quality:** THD+N < 0.001%
- **Performance:** <50ms latency, >8h battery life
- **Stability:** <0.1% crash rate
- **Compatibility:** 99%+ Android devices (API 24+)

### User Metrics
- **App Store rating:** >4.7/5 stars
- **User retention:** >80% after 30 days
- **Feature adoption:** >60% users engage with advanced features
- **Support efficiency:** <1% users need support

### Business Metrics
- **Market position:** Top 3 in audiophile category
- **Revenue growth:** $100K+ monthly within 6 months
- **User acquisition:** 100K+ active users within first year
- **Industry recognition:** Featured by major tech publications

## Communication Guidelines

### Working with Claude

#### Effective Prompting
- **Be specific** about requirements and constraints
- **Provide context** about current project state
- **Ask focused questions** rather than broad requests
- **Include error messages** and specific problems
- **Specify output format** (code, documentation, analysis)

#### Context Preservation
- **Save important discussions** for future reference
- **Document architectural decisions** made with Claude
- **Keep track of feature implementation** progress
- **Maintain changelog** of Claude-assisted developments

#### Iteration Process
- **Start simple** and build complexity gradually
- **Test frequently** and validate assumptions
- **Refactor continuously** for better code quality
- **Document learnings** from each iteration
- **Celebrate achievements** and milestones

## Project Timeline and Milestones

### Phase 1: Foundation (Weeks 1-2)
- Basic audio playback engine
- Cyberpunk UI foundation
- Development environment setup
- Initial testing framework

### Phase 2: Audio Enhancement (Weeks 3-4)
- 10-band graphic equalizer
- Sub-bass enhancement system
- Audio format support expansion
- Performance optimization

### Phase 3: Advanced Features (Weeks 5-8)
- 32-band parametric EQ upgrade
- Hi-res audio support
- Neural network visualizations
- Advanced UI components

### Phase 4: Intelligence Layer (Weeks 9-12)
- AI-powered audio analysis
- Smart EQ recommendations
- Voice control integration
- Machine learning features

### Phase 5: Premium Features (Weeks 13-16)
- Cloud synchronization
- Advanced audio analysis tools
- Premium subscription features
- Final polish and optimization

## Maintenance and Evolution

### Ongoing Development
- **Regular updates** with new features and bug fixes
- **Performance monitoring** and optimization
- **User feedback integration** into development roadmap
- **Technology stack updates** and improvements

### Community Engagement
- **Open source components** where appropriate
- **Developer documentation** for extensibility
- **User community building** around the product
- **Feedback collection** and feature request management

## GitHub Copilot Best Practices

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

### Key Principles
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

### Logging Best Practices
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

### Placeholder Implementation Standards
```kotlin
// ❌ BAD: Uninitialized arrays that may cause confusion
return FloatArray(128) // Unclear what this represents

// ❌ BAD: Misleading comments about configurability
return FloatArray(FEATURE_SIZE) // Configurable feature vector size (but it's actually hardcoded)

// ✅ GOOD: Clear placeholder documentation with explicit initialization
// Extract MFCC, spectral centroid, zero crossing rate, etc.
// Returns placeholder feature vector until TensorFlow Lite models are integrated
return FloatArray(AUDIO_FEATURE_VECTOR_SIZE) { 0.0f }
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

### State Validation Order Best Practices
```kotlin
// ❌ BAD: Checking sub-state before main state
suspend fun processAudio(data: FloatArray): FloatArray {
    if (!modelsLoaded) {
        return data  // Could be uninitialized but modelsLoaded = true
    }
    // Process audio...
}

// ✅ GOOD: Validate initialization state first, then sub-states
suspend fun processAudio(data: FloatArray): FloatArray {
    require(data.isNotEmpty()) { "Audio buffer cannot be empty" }
    check(isInitialized) { "Neural processor must be initialized before processing" }
    
    if (!modelsLoaded) {
        Log.w(TAG, "Models not loaded, returning original buffer")
        return data
    }
    // Process audio...
}
```

### Magic Number Extraction Standards
```kotlin
// ❌ BAD: Magic numbers scattered throughout algorithm functions
private fun enhanceForWorkout(eq: List<Float>): List<Float> {
    for (i in 0..6) bands[i] += 2.0f      // What does 2.0f represent?
    for (i in 20..26) bands[i] += 1.5f    // What frequency range?
    val comp = (noise - 40f) * 0.1f       // Why 40f and 0.1f?
}

// ✅ GOOD: Named constants with clear semantic meaning
companion object {
    // EQ Enhancement Constants
    private const val WORKOUT_BASS_BOOST = 2.0f            // Sub-bass enhancement
    private const val WORKOUT_PRESENCE_BOOST = 1.5f        // 2-8kHz clarity
    private const val COMMUTE_NOISE_BASELINE = 40f         // Quiet environment baseline (dB)
    private const val COMMUTE_COMPENSATION_SCALE = 0.1f    // Linear compensation factor
}

private fun enhanceForWorkout(eq: List<Float>): List<Float> {
    for (i in 0..6) bands[i] += WORKOUT_BASS_BOOST         // Sub-bass enhancement for energy
    for (i in 20..26) bands[i] += WORKOUT_PRESENCE_BOOST   // Presence boost for clarity
    val comp = (noise - COMMUTE_NOISE_BASELINE) * COMMUTE_COMPENSATION_SCALE
}
```

### Public API Safety Standards
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

These patterns ensure Copilot reviews pass and maintain professional code quality standards.

### Build Artifacts and Repository Management
```
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
- **See `/docs/APK_TESTING_GUIDE.md` for comprehensive APK management guidelines**

---

*This document serves as the living guide for the FTL Hi-Res Audio Player project development. It should be updated regularly as the project evolves and new insights are gained through AI-assisted development.*
- https://github.com/subc0der/ftl-ai-hi-res-audio-player
- give descriptive names to APK test files, number them sequentially, and save them in /apk-test-packages
- GitHub Repo: https://github.com/subc0der/ftl-player