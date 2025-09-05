# 🎵 FTL Hi-Res Audio Player

```
╔══════════════════════════════════════════════════════════════╗
║                    F T L   A U D I O   P L A Y E R           ║
║                                                              ║
║         ◢████████████████████████████████████████████◣       ║
║        ◢██▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓██◣      ║
║       ◢██▓▓░ The World's Most Advanced Audiophile ▓▓██◣     ║
║      ◢██▓▓░    Music Player for Android    ░▓▓██◣         ║  
║     ◢██▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓██◣        ║
║    ◥████████████████████████████████████████████◤         ║
║                                                              ║
║    ⚡ CYBER ENHANCED ⚡ AI POWERED ⚡ AUDIOPHILE GRADE ⚡     ║
╚══════════════════════════════════════════════════════════════╝
```

**⚡ Neural-Enhanced Audio Experience • 32-bit/768kHz Hi-Res • AI-Powered EQ ⚡**

---

## 🚀 Project Overview

**FTL Hi-Res Audio Player** is an AI-assisted development project creating the ultimate premium audiophile music player with cutting-edge technology and cyberpunk aesthetics.

### 🎯 Key Features

- **🎛️ 32-Band Parametric EQ** with individual Q-factor controls (0.1-30.0)
- **🎵 Hi-Res Audio Support** - Up to 32-bit/768kHz, DSD512, MQA decoding
- **🤖 AI-Powered Intelligence** - Adaptive EQ, smart playlists, mood detection
- **🏋️ Fitness Integration** - Workout timers, biometric sync, sleep intelligence
- **🎨 Cyberpunk Design** - Neural network animations, dark-first UI
- **⚡ Ultra-Low Latency** - <10ms audio processing, 120fps UI target

---

## 🎨 Design Language

### Color Palette
```css
Primary: Fluorescent Aquamarine (#00FFFF)  /* Main accents, active states */
Accent: Indigo (#4B0082)                   /* Secondary highlights, controls */
Background: Black (#000000)                /* Dark-first design foundation */
```

### Typography
- **Headers:** Orbitron Black (cyberpunk aesthetic)
- **Body:** Orbitron Regular (UI elements)
- **Technical:** JetBrains Mono (data displays)
- **Readable:** Inter (body text where needed)

---

## 📋 Development Status

### ✅ Phase 3 Complete: Enhanced Now Playing Screen (v1.0.1-008)
- ✅ **Interactive Now Playing Screen** - Seek bar, skip controls, album artwork
- ✅ **Background Playback Service** - MediaSessionCompat with notification controls
- ✅ **Lock Screen Integration** - Media controls with proper lifecycle management
- ✅ **Splash Screen** - Custom branding with mercsev.png logo
- ✅ **Music Library** - Scanning, display, and track selection
- ✅ **Stable Playback** - Crash-free skip navigation with debounce protection

### 🚀 Ready for Phase 4: Queue & Playlist Management
- 🎯 **Next Features:** Queue visualization, drag-to-reorder, playlist CRUD, shuffle/repeat modes
- 🔧 **Current Build:** v1.0.1-008 - All critical bugs resolved
- 🏗️ **Foundation:** Clean Architecture + MVVM + Hilt DI established

### Development Progress
- ✅ **Phases 1-3 Complete** - Core music playback fully functional
- ✅ **Architecture Established** - Clean separation, dependency injection, testing
- ✅ **UI Foundation** - Jetpack Compose with indigo/cyan cyberpunk theme
- ✅ **Technical Foundation** - Room database, MediaPlayer integration, background services

---

## 🏗️ Architecture Overview

### **Clean Architecture + MVVM**
```
📱 Presentation Layer (Compose UI + ViewModels)
├── Screens: Library, Now Playing, Enhanced EQ, Settings
├── Components: Custom cyberpunk UI elements
└── ViewModels: Reactive state management

🧠 Domain Layer (Business Logic)
├── Use Cases: Audio processing, playlist management
├── Repositories: Abstract data access interfaces
└── Models: Core business entities

📊 Data Layer (Local + Remote)
├── Room Database: Music library, settings, analysis
├── Audio Engine: DSP processing, format support
└── External APIs: Metadata, cloud sync
```

### **Key Technologies**
- **Language:** Kotlin with modern idioms
- **UI:** Jetpack Compose with custom animations
- **Architecture:** MVVM + Clean Architecture + Hilt DI
- **Database:** Room with comprehensive schema
- **Audio:** Media3 + custom DSP engine
- **Testing:** Unit tests, integration tests, UI tests

---

## 📁 Repository Structure

```
ftl-player/
├── docs/                     # Complete project documentation
│   ├── claude_md_file.md     # Claude integration guidelines
│   ├── music_player_prd.md   # Product requirements document
│   ├── ftl_added_features.md # Fitness & wellness features
│   ├── ftl_custom_settings_spec.md # 200+ settings system
│   ├── settings_overview.md  # Settings quick reference
│   └── project_structure.md  # Architecture documentation
├── CLAUDE.md                 # AI-assisted development guide
├── HANDOFF_SUMMARY.md        # Development progress summary
└── README.md                 # This file
```

---

## 🎯 Success Metrics

### **Technical KPIs**
- **Audio Quality:** THD+N < 0.001%, frequency response ±0.1dB
- **Performance:** <50ms total latency, >8h battery life
- **Compatibility:** 99%+ Android devices (API 24+)
- **Stability:** <0.1% crash rate

### **User Experience KPIs**
- **App Store Rating:** >4.7/5 stars target
- **Feature Adoption:** >60% users engage with advanced features
- **User Retention:** >80% after 30 days

---

## 💻 Code Style Requirements

### **Kotlin Standards**
- **Modern idioms** - Latest Kotlin features, data classes, sealed classes
- **Coroutines for async** - All background work uses coroutines
- **Null safety** - Proper nullable types, avoid `!!` operator

### **Jetpack Compose**
- **State hoisting** - State flows up, events flow down
- **Proper lifecycle handling** - Correct use of `remember`, `LaunchedEffect`
- **Performance optimization** - Use `key()`, avoid unnecessary recompositions

### **Architecture**
- **Clean separation** - Clear boundaries between layers
- **MVVM + Clean Architecture** - ViewModels, Use Cases, Repository pattern
- **Dependency injection** - Hilt for DI, avoid manual instantiation

### **Testing & Documentation**
- **Unit test structure** - Every major component has tests
- **KDoc for public APIs** - All public classes and functions documented
- **Usage examples** - Complex APIs include examples

---

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Flamingo+
- Kotlin 1.9+
- Android SDK 24+ (targeting 34)
- Git for version control

### **Development Setup**
```bash
# Clone the repository
git clone https://github.com/subc0der/ftl-player.git
cd ftl-player

# Build the project
./gradlew assembleDebug

# Install on device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

### **Current Features**
- 🎵 **Music Library Scanning** - Automatic detection of MP3/FLAC files
- 🎛️ **Now Playing Screen** - Full playback controls with real-time position
- 🔄 **Skip Navigation** - Previous/Next track with crash protection
- 📱 **Background Playback** - Continues playing when app is minimized
- 🔒 **Lock Screen Controls** - Media controls on lock screen and notification
- 🎨 **Cyberpunk UI** - Dark theme with indigo/cyan accents
- ⚡ **Performance** - Smooth 60fps UI with optimized audio processing

---

## 📖 Documentation

- **[Complete PRD](docs/music_player_prd.md)** - Product requirements and feature specifications
- **[Architecture Guide](docs/project_structure.md)** - Technical architecture and setup
- **[Settings System](docs/ftl_custom_settings_spec.md)** - Comprehensive customization system
- **[Claude Integration](CLAUDE.md)** - AI-assisted development workflow
- **[Development Progress](HANDOFF_SUMMARY.md)** - Current status and next steps

---

## 🤝 Contributing

This is an AI-assisted development project using Claude for implementation. See [CLAUDE.md](CLAUDE.md) for development workflow and guidelines.

---

## 📄 License

**Proprietary** - All rights reserved. This is a private development project.

---

**Built with ❤️ and 🤖 Claude AI**  
*Creating the future of mobile audio experiences*

🎵⚡✨