# Lessons Learned - FTL Hi-Res Audio Player Development

## File Management & Build System

### ❌ **NEVER Create Duplicate Files**
**Issue:** Duplicate configuration files can cause build failures and unpredictable behavior
**Examples:**
- Multiple `settings.gradle.kts` files
- Duplicate `.claude` directories  
- Multiple configuration files of the same type

**Solution:** Always check if file exists before creating. Use single source of truth for all configuration.

---

## Project Structure

### ❌ **Avoid Inconsistent Directory Structure**
**Issue:** Mixed folder hierarchies can break Clean Architecture patterns
**Solution:** Maintain consistent separation between data/domain/presentation layers

---

## GitHub Copilot Performance Issues

### ❌ **Performance-Critical Code Validation**
**Issue:** Using `require()` calls in audio processing paths introduces exception overhead
**Found in:** `AudioEngine.kt:processAudioBuffer()`, `NeuralAudioProcessor.kt:analyzeAudio()`, `NeuralAudioProcessor.kt:enhanceAudioQuality()`
**Performance Impact:** Exception creation, string formatting, and stack traces in real-time audio callbacks
**Solution:** Use lightweight validation in performance-critical paths:
```kotlin
// ❌ BAD - Heavy exception overhead
require(audioBuffer.isNotEmpty()) { "Audio buffer cannot be empty" }

// ✅ GOOD - Lightweight validation with graceful fallback
if (audioBuffer.isEmpty()) return null
```

### ❌ **Hardcoded JNI Signatures**
**Issue:** Hardcoded JNI signature strings are fragile and difficult to maintain
**Found in:** `jni_helpers.cpp:36`
**Solution:** Extract to named constants with documentation:
```cpp
// ❌ BAD - Magic string that's hard to maintain
jmethodID constructor = env->GetMethodID(clazz, "<init>", "(DDJJDDJJD)V");

// ✅ GOOD - Named constant with clear documentation
namespace JNISignatures {
    // D = double, J = long, V = void
    static const char* PERFORMANCE_METRICS_CONSTRUCTOR = "(DDJJDDJJD)V";
}
jmethodID constructor = env->GetMethodID(clazz, "<init>", JNISignatures::PERFORMANCE_METRICS_CONSTRUCTOR);
```

### ❌ **Repeated Math Calculations in Loops**
**Issue:** Calculating same mathematical expressions repeatedly in loops wastes CPU
**Found in:** `AudioEngineTestViewModel.kt:240` (sine wave generation)
**Solution:** Pre-calculate constants outside loops:
```kotlin
// ❌ BAD - Repeated calculation in every iteration
val testBuffer = FloatArray(2000) { index ->
    (0.5 * sin(2.0 * PI * 440.0 * sample / 48000.0)).toFloat()
}

// ✅ GOOD - Pre-calculated constant
val sineWaveConstant = 2.0 * PI * 440.0 / 48000.0
val testBuffer = FloatArray(2000) { index ->
    (0.5 * sin(sineWaveConstant * sample)).toFloat()
}
```

### 📋 **Copilot Review Best Practices**
1. **Proactively search for similar patterns** across entire codebase when fixing issues
2. **Performance validation in audio paths** - avoid heavy exception handling
3. **Extract JNI signatures** to maintainable named constants
4. **Pre-calculate loop constants** for mathematical operations
5. **Test build after each fix** to ensure no regressions

### ✅ **Use Professional Gradle Configuration**
**Lesson:** Modern Gradle with version catalogs (libs.versions.toml) prevents dependency conflicts
**Implementation:** Single dependency source with bundles for related libraries

---

## Development Workflow

### ❌ **Don't Skip Foundation Verification**
**Issue:** Building on incomplete foundation leads to cascading issues
**Solution:** Always verify complete project structure before implementing features

### ✅ **Document Architecture Decisions**
**Lesson:** Use .claude/context/ folder to maintain development context across sessions
**Implementation:** ADR (Architecture Decision Records) for major technical choices

---

## Android-Specific

### ❌ **Missing Essential Configuration Files**
**Issue:** Android projects require specific files for proper building
**Critical Files:**
- `local.properties` (SDK path)
- `gradle.properties` (AndroidX configuration)  
- `AndroidManifest.xml` (permissions and components)
- `proguard-rules.pro` (release optimization)

### ✅ **Use Proper Permission Declaration**
**Lesson:** Audio apps need comprehensive permission setup for all Android versions
**Implementation:** Target SDK-specific permissions with proper fallbacks

---

## Code Quality

### ❌ **Avoid Hardcoded Values**
**Issue:** Magic numbers and strings make code unmaintainable
**Solution:** Use resources (strings.xml, colors.xml) and constants

### ✅ **Follow Clean Architecture Principles**
**Lesson:** Strict separation of concerns prevents tight coupling
**Layers:** Presentation → Domain → Data (unidirectional dependency flow)

---

## Build Optimization

### ❌ **Don't Ignore ProGuard Configuration**
**Issue:** Missing ProGuard rules can break audio processing in release builds
**Solution:** Comprehensive rules for audio libraries, DI, and serialization

### ✅ **Optimize for Audio Performance**
**Lesson:** Audio apps need specific compiler optimizations
**Implementation:** Native C++ build configuration with -O3 and audio-specific flags

---

## Development Context

### ✅ **Maintain Claude Integration Standards**
**Lesson:** Proper .claude folder structure improves AI-assisted development
**Structure:**
```
.claude/
├── context/ (project state, milestones, decisions)
├── templates/ (code templates for consistency)
├── prompts/ (reusable development prompts)
└── sessions/ (development logs and reviews)
```

### ✅ **Use Todo Management**
**Lesson:** Track progress systematically to avoid missing requirements
**Implementation:** TodoWrite tool for milestone and task tracking

---

## Color Scheme & Design

### ❌ **Don't Misinterpret Design Requirements**
**Issue:** Color scheme misunderstanding can break brand consistency
**Solution:** Always confirm: "Dark-first design with Indigo + Fluorescent Aquamarine accents"
**Correct Palette:**
- Primary: Indigo (#4B0082)
- Secondary: Fluorescent Aquamarine (#00FFFF)  
- Background: Pure Black (#000000)

---

## Version Control

### ✅ **Commit Foundation Before Features**
**Lesson:** Stable foundation commits prevent rollback issues
**Practice:** Complete project setup → commit → then implement features

### ❌ **Don't Commit Large Binary Files to Git**
**Issue:** APK files (65MB+) cause repository bloat
**Solution:** Use .gitignore for build outputs, keep only source code

---

## Testing & Quality Assurance

### ✅ **Verify Build System Before Implementation**
**Lesson:** Gradle configuration issues compound during development
**Process:** Create foundation → test build → implement features

### ❌ **Don't Skip Architecture Validation**
**Issue:** Poor architecture decisions are expensive to fix later
**Solution:** Use documentation review and ADR process for major decisions

---

## Future Additions

*This section will be updated as we encounter new lessons during development*

---

**Remember: Prevention is better than debugging. Always double-check file existence and architecture consistency before creating new components.**

---

*Last Updated: September 1, 2025*  
*Development Phase: Foundation Complete*
