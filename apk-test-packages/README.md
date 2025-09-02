# APK Test Packages Directory

This directory contains test APK builds for the FTL Hi-Res Audio Player project.

## ⚠️ Important Notes

- **This directory is excluded from git** via `.gitignore`
- **APK files should NEVER be committed** to version control
- **Follow naming conventions** as defined in `/docs/APK_TESTING_GUIDE.md`

## Directory Structure

```
apk-test-packages/
├── README.md                      # This file
├── v1.0.0/                       # Version-specific builds
├── v1.1.0/                       # Future version builds
└── experimental/                 # Prototype/experimental builds
```

## Naming Convention

**Standard builds:**
```
ftl-player-v{VERSION}-{SEQUENCE}-{DESCRIPTION}.apk
```

**Examples:**
- `ftl-player-v1.0.0-001-debug.apk`
- `ftl-player-v1.0.0-002-eq-feature.apk`
- `ftl-player-v1.1.0-001-ai-features.apk`

**Experimental builds:**
```
ftl-player-exp-{SEQUENCE}-{FEATURE}.apk
```

**Examples:**
- `ftl-player-exp-001-neural-engine.apk`
- `ftl-player-exp-002-biometric-eq.apk`

## Quick Commands

### Build and Store Debug APK
```bash
# Build
./gradlew assembleDebug

# Copy with proper naming (update version/sequence/description)
cp app/build/outputs/apk/debug/app-debug.apk \
   apk-test-packages/v1.0.0/ftl-player-v1.0.0-001-debug.apk
```

### Install on Device
```bash
# Install specific build
adb install apk-test-packages/v1.0.0/ftl-player-v1.0.0-001-debug.apk

# Update existing installation
adb install -r apk-test-packages/v1.0.0/ftl-player-v1.0.0-002-eq-feature.apk
```

### Clean Old Builds
```bash
# Keep only latest 5 builds per version
find apk-test-packages/v1.0.0 -name "*.apk" | sort -V | head -n -5 | xargs rm
```

## For Complete Documentation

See `/docs/APK_TESTING_GUIDE.md` for comprehensive guidelines including:
- Detailed naming conventions
- Build workflow procedures
- Quality assurance steps
- Automation scripts
- Troubleshooting guide

## Directory Management

- **Maximum 5 APKs per version directory** (clean older builds)
- **Create new version directories** as needed
- **Use experimental/** for prototype builds
- **Document build purposes** and test results