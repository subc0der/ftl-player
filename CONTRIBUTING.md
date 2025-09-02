# 🤝 Contributing to FTL Hi-Res Audio Player

```
╔══════════════════════════════════════════════════════════════════════════════════╗
║                      🤝 NEURAL CONTRIBUTION PROTOCOL 🤝                        ║
║                   AI-Assisted Development Guidelines                            ║
║                                                                                  ║
║    🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵     ║
╚══════════════════════════════════════════════════════════════════════════════════╝
```

**Welcome to the Neural Audio Development Matrix!** Thank you for contributing to the FTL Hi-Res Audio Player. This guide explains our AI-assisted development workflow, branch protection policies, and contribution standards.

---

## 🚀 Quick Contribution Guide

```yaml
1. Fork & Clone:     Fork repository, clone locally
2. Branch:          Create feature branch from develop
3. Code:            Implement with neural quality standards
4. Test:            Run tests, verify <10ms latency
5. Commit:          Use conventional commits
6. Push:            Push to your fork
7. PR:              Open PR to develop branch
8. Review:          Address feedback, maintain standards
9. Merge:           Celebrate your neural audio contribution!
```

---

## 🎯 Development Workflow

### 🔄 Git Flow Model

We follow a modified Git Flow with neural enhancement checkpoints:

```
master/main (production)
    ↑
    │ Release PR (2 reviews + all checks)
    │
develop (integration)
    ↑
    │ Feature PR (1 review + checks)
    │
feature/* (development)
    ↑
    │ Your commits
    │
your-fork/feature
```

### 🌲 Branch Structure

```yaml
Protected Branches:
  master/main:      Production-ready code (heavily protected)
  develop:          Integration branch (protected)
  
Feature Branches:
  feature/*:        New features (from develop)
  bugfix/*:         Bug fixes (from develop)
  hotfix/*:         Emergency fixes (from master)
  release/*:        Release preparation (from develop)
  
Naming Convention:
  feature/audio-engine-optimization
  feature/32-band-eq-enhancement
  bugfix/neural-processing-crash
  hotfix/critical-latency-issue
```

---

## 🔐 Branch Protection Rules

### 🛡️ Master/Main Branch Protection

```yaml
Requirements:
  ✅ 2 approved reviews (including code owner)
  ✅ All CI/CD checks must pass:
      - Neural Code Quality Analysis
      - Audiophile Unit Tests (all API levels)
      - Neural Audio APK Generation
      - Cyberpunk Security Analysis
  ✅ Branch must be up to date with master
  ✅ Conversations must be resolved
  ✅ Administrators included in restrictions
  ✅ No force pushes allowed
  ✅ No direct commits (PR only)
```

### 🔧 Develop Branch Protection

```yaml
Requirements:
  ✅ 1 approved review minimum
  ✅ Core CI/CD checks must pass:
      - Neural Code Quality Analysis
      - Unit Tests (API 34)
      - APK Generation
  ✅ Branch must be up to date
  ✅ Conversations must be resolved
  ✅ No force pushes allowed
  ✅ Admins can bypass for emergencies
```

---

## 📝 Commit Message Convention

We use **Conventional Commits** with neural audio context:

### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
```yaml
feat:     New feature (audio engine, UI, AI)
fix:      Bug fix (crash, audio glitch, latency)
docs:     Documentation updates
style:    Code style (formatting, no logic change)
refactor: Code restructuring (no feature change)
perf:     Performance improvements (<10ms target)
test:     Test additions or corrections
chore:    Build, tools, dependencies
```

### Scopes
```yaml
audio:    Audio engine, DSP, processing
eq:       32-band parametric equalizer
ui:       Cyberpunk UI/UX components
ai:       Neural AI features, ML models
fitness:  Biometric and workout features
settings: 200+ settings system
api:      API changes
ci:       CI/CD pipeline changes
```

### Examples
```bash
# Feature commit
feat(audio): implement <10ms latency optimization for DSD playback

Add multi-threaded DSP processing with lock-free buffers
to achieve sub-10ms latency for DSD audio formats.

- Implement ring buffer with atomic operations
- Add SIMD optimizations for ARM processors
- Test verified 7.2ms average latency

Closes #123

# Bug fix commit
fix(eq): resolve Q-factor precision loss in 32-band EQ

Fixed floating-point precision issues causing Q-factor
drift in bands 25-32 during real-time adjustment.

- Use 64-bit doubles for Q-factor calculations
- Add bounds checking for extreme values
- Maintain precision across save/load cycles

Fixes #456

# Performance commit
perf(ai): optimize neural inference to <50ms

Reduce TensorFlow Lite model inference time by 40%
through quantization and operator fusion.

- Apply int8 quantization to non-critical layers
- Fuse conv2d and relu operations
- Cache frequently accessed tensors

Neural processing now averages 42ms (target: <50ms)
```

---

## 🤖 AI-Assisted Development with Claude

### 🧠 Claude Code Integration

We leverage Claude AI for enhanced development:

```yaml
Recommended Uses:
  ✅ Code generation for complex algorithms
  ✅ DSP optimization suggestions
  ✅ Architecture design discussions
  ✅ Test case generation
  ✅ Documentation writing
  ✅ Performance optimization ideas
  ✅ Code review assistance

Best Practices:
  - Include context from CLAUDE.md
  - Specify audio quality targets
  - Request cyberpunk aesthetic compliance
  - Verify <10ms latency requirements
  - Test AI-generated code thoroughly
```

### 📋 Using CLAUDE.md

Always reference our [CLAUDE.md](CLAUDE.md) file when working with Claude:

1. **Context Setup:** Provide current feature context
2. **Performance Targets:** Specify latency and quality requirements
3. **Code Standards:** Request Kotlin best practices
4. **Testing Requirements:** Ask for test coverage
5. **Documentation:** Generate comprehensive docs

---

## 🧪 Testing Requirements

### ✅ Required Tests

```yaml
Unit Tests:
  - Minimum 80% code coverage
  - All public APIs must have tests
  - Audio processing accuracy tests
  - Performance regression tests

Integration Tests:
  - Audio pipeline end-to-end
  - EQ parameter changes
  - Neural AI inference
  - Biometric data flow

UI Tests:
  - Critical user flows
  - Cyberpunk theme rendering
  - 120fps animation verification
  - Accessibility compliance

Performance Tests:
  - Audio latency <10ms verification
  - Memory usage <200MB
  - Battery drain <10%/hour
  - CPU usage <15% normal operation
```

### 🎵 Audio Quality Tests

```kotlin
@Test
fun testAudioLatency() {
    // Verify <10ms total pipeline latency
    val latency = audioEngine.measureLatency()
    assertThat(latency).isLessThan(10.milliseconds)
}

@Test
fun testEQPrecision() {
    // Verify 32-band EQ precision
    val eq = ParametricEqualizer32()
    eq.setBandGain(15, 3.5) // Band 15, +3.5dB
    assertThat(eq.getBandGain(15)).isWithin(0.01).of(3.5)
}

@Test
fun testNeuralInference() {
    // Verify AI inference <50ms
    val startTime = System.nanoTime()
    val result = neuralProcessor.analyzeAudio(audioBuffer)
    val inferenceTime = (System.nanoTime() - startTime) / 1_000_000
    assertThat(inferenceTime).isLessThan(50)
}
```

---

## 🎨 Code Style & Standards

### 🔧 Kotlin Style Guide

```kotlin
// ═══════════════════════════════════════════════════════════════
// File header with neural documentation
// ═══════════════════════════════════════════════════════════════

package com.ftl.audioplayer.audio

/**
 * Neural-enhanced audio processor with <10ms latency target
 * 
 * @property sampleRate Current sample rate (up to 768kHz)
 * @property bufferSize Audio buffer size (64-2048 samples)
 */
class NeuralAudioProcessor(
    private val sampleRate: Int = 96000,
    private val bufferSize: Int = 256
) {
    companion object {
        const val TARGET_LATENCY_MS = 10
        const val MAX_SAMPLE_RATE = 768000
        const val THD_N_TARGET = 0.001 // <0.001% THD+N
    }
    
    /**
     * Process audio with neural enhancement
     * 
     * @param audioBuffer Input audio samples
     * @return Enhanced audio with <10ms processing time
     */
    suspend fun processAudio(
        audioBuffer: FloatArray
    ): FloatArray = withContext(Dispatchers.Default) {
        // Neural processing implementation
        enhanceWithAI(audioBuffer)
    }
}
```

### 🎨 Cyberpunk UI Standards

```kotlin
// Cyberpunk color palette constants
object CyberpunkColors {
    val CyberAqua = Color(0xFF00FFFF)    // Primary accent
    val NeuralIndigo = Color(0xFF4B0082)  // Secondary
    val PureBlack = Color(0xFF000000)     // Background
    val NeuralGreen = Color(0xFF00FF66)   // AI activity
}

// Animation specifications
object NeuralAnimations {
    const val TARGET_FPS = 120
    const val STANDARD_DURATION = 300L
    const val NEURAL_PULSE_DURATION = 1000L
}
```

---

## 📊 Performance Standards

### ⚡ Mandatory Performance Targets

```yaml
Audio Performance:
  Total Latency:        <10ms (5-8ms typical)
  THD+N:               <0.001% @ 1kHz
  Frequency Response:   ±0.1dB (20Hz-20kHz)
  Dynamic Range:        >120dB
  
App Performance:
  Cold Start:          <500ms
  Memory Usage:        <200MB total
  Battery Drain:       <10% per hour
  CPU Usage:           <15% normal playback
  
Neural AI:
  Inference Time:      <50ms
  Model Size:          <100MB
  Accuracy:            >90% genre detection
```

### 📈 Performance Testing

```bash
# Run performance benchmarks
./gradlew benchmark

# Measure audio latency
./gradlew measureAudioLatency

# Profile memory usage
./gradlew profileMemory

# Check battery impact
./gradlew batteryProfile
```

---

## 🚀 Pull Request Process

### 1️⃣ Before Creating PR

```yaml
Checklist:
  ✅ Code follows Kotlin style guide
  ✅ All tests pass locally
  ✅ Performance targets met
  ✅ Documentation updated
  ✅ Commit messages follow convention
  ✅ Branch is up to date with target
```

### 2️⃣ PR Template Usage

Use our [PR template](.github/pull_request_template.md):

```markdown
## 🎯 Enhancement Summary
Brief description of neural enhancement

## 📋 Neural Feature Description
Detailed implementation and impact

## ✅ Testing Checklist
- [ ] Audio latency <10ms verified
- [ ] Unit tests pass (80%+ coverage)
- [ ] UI renders at 120fps
- [ ] Memory usage <200MB

## 🎨 Cyberpunk Compliance
- [ ] Uses correct color palette
- [ ] Animations smooth at 120fps
- [ ] Neural effects implemented
```

### 3️⃣ Review Process

```yaml
Review Timeline:
  First Response:    Within 48 hours
  Review Completion: Within 1 week
  
Review Focus:
  - Code quality and standards
  - Performance impact (<10ms latency)
  - Test coverage (80%+ target)
  - Cyberpunk aesthetic compliance
  - Neural AI integration quality
```

### 4️⃣ Merge Requirements

```yaml
To Develop:
  ✅ 1 approved review
  ✅ All CI checks pass
  ✅ Up to date with develop
  ✅ Conversations resolved
  
To Master:
  ✅ 2 approved reviews
  ✅ Code owner approval
  ✅ All CI/CD checks pass
  ✅ Security scan clean
  ✅ Release notes prepared
```

---

## 🏆 Recognition & Credits

### 🌟 Contributors

We value all contributions! Contributors are recognized in:

- **README.md** - Top contributors section
- **Release Notes** - Per-release contributors
- **Wiki** - Monthly neural audio champions
- **Code** - Author tags in significant features

### 🎵 Contribution Levels

```yaml
🎵 Audio Apprentice:    First PR merged
🎛️ EQ Expert:          5+ audio-related PRs
🤖 Neural Navigator:    AI/ML feature contributions
🎨 Cyberpunk Creator:   UI/UX enhancements
⚡ Performance Pro:     Optimization contributions
🏆 Core Contributor:    Regular, high-quality contributions
```

---

## 🔧 Development Setup

### Prerequisites

```bash
# Required tools
- Android Studio Arctic Fox+
- Kotlin 1.9+
- Android SDK 34
- Git 2.x
- GitHub CLI (optional but recommended)

# Clone repository
git clone https://github.com/subc0der/ftl-player.git
cd ftl-player

# Install Git hooks
./scripts/install-hooks.sh

# Setup branch protection (maintainers only)
./.github/branch-protection-rules.sh
```

### Building

```bash
# Debug build
./gradlew assembleDebug

# Run tests
./gradlew test

# Check code quality
./gradlew ktlintCheck
./gradlew detekt

# Generate APK with checks
./gradlew build
```

---

## 📚 Resources

### 🔗 Important Links

- **[Documentation Hub](docs/README.md)** - Complete knowledge base
- **[CLAUDE.md](CLAUDE.md)** - AI development guide
- **[Audio Specifications](docs/specifications/audio-specs.md)** - Technical standards
- **[Architecture Overview](docs/architecture/system-overview.md)** - System design
- **[GitHub Issues](https://github.com/subc0der/ftl-player/issues)** - Bug reports
- **[Discussions](https://github.com/subc0der/ftl-player/discussions)** - Community forum

### 📖 Learning Resources

- **[Kotlin Documentation](https://kotlinlang.org/docs/)**
- **[Android Audio Guide](https://developer.android.com/guide/topics/media)**
- **[DSP Basics](https://www.dspguide.com/)**
- **[TensorFlow Lite](https://www.tensorflow.org/lite)**

---

## ❓ Getting Help

### 💬 Communication Channels

```yaml
Questions:           GitHub Discussions
Bug Reports:         GitHub Issues
Feature Requests:    GitHub Issues (enhancement label)
Security Issues:     Email security@ftl-audio.dev
General Discussion:  GitHub Discussions
```

### 🆘 Support Process

1. **Check Documentation** - Review relevant guides
2. **Search Issues** - Look for similar problems
3. **Ask in Discussions** - Community support
4. **Create Issue** - If bug confirmed
5. **PR Welcome** - We appreciate fixes!

---

## 🛡️ Security

### 🔐 Reporting Security Issues

**DO NOT** create public issues for security vulnerabilities.

Instead:
1. Email security@ftl-audio.dev
2. Include detailed description
3. Wait for response (within 48 hours)
4. Coordinate disclosure timeline

### 🔑 Security Standards

```yaml
Required:
  - No hardcoded secrets/keys
  - Input validation on all user data
  - Secure network communications
  - Privacy-compliant data handling
  - Local-only AI processing option
```

---

## 📜 Code of Conduct

### 🤝 Our Standards

```yaml
Expected Behavior:
  ✅ Respectful and inclusive language
  ✅ Constructive feedback
  ✅ Focus on neural audio excellence
  ✅ Help others learn and grow
  ✅ Celebrate achievements
  
Unacceptable:
  ❌ Harassment or discrimination
  ❌ Trolling or insulting comments
  ❌ Public or private harassment
  ❌ Publishing private information
  ❌ Inappropriate content
```

### ⚖️ Enforcement

Violations may result in:
1. Warning
2. Temporary ban
3. Permanent ban

Report issues to conduct@ftl-audio.dev

---

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  🤝 WELCOME TO THE NEURAL AUDIO DEVELOPMENT COMMUNITY 🤝                   │
│                                                                             │
│  🎵 Build the future of mobile audio                                       │
│  🤖 Leverage AI for enhanced development                                   │
│  🎨 Maintain cyberpunk aesthetic excellence                                │
│  ⚡ Achieve <10ms latency perfection                                       │
│  🎛️ Master 32-band parametric precision                                    │
│                                                                             │
│  Your contributions shape the neural audio revolution! 🎵⚡🤖              │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Thank you for contributing to the FTL Hi-Res Audio Player!** 🎵✨

*Together, we're creating the future of neural-enhanced mobile audio experiences.*