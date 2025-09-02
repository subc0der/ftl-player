# APK Testing Guide

## Overview

This document defines the standards for building, naming, storing, and managing APK files for testing the FTL Hi-Res Audio Player.

## Directory Structure

```
ftl-player/
├── apk-test-packages/              # Test APK storage (excluded from git)
│   ├── README.md                   # This guide reference
│   ├── v1.0.0/                    # Version-specific directories
│   │   ├── ftl-player-v1.0.0-001-debug.apk
│   │   ├── ftl-player-v1.0.0-002-eq-feature.apk
│   │   └── ftl-player-v1.0.0-003-ui-update.apk
│   ├── v1.1.0/
│   │   ├── ftl-player-v1.1.0-001-ai-features.apk
│   │   └── ftl-player-v1.1.0-002-performance.apk
│   └── experimental/               # Experimental builds
│       ├── ftl-player-exp-001-neural-engine.apk
│       └── ftl-player-exp-002-biometric-eq.apk
```

## APK Naming Convention

### Standard Release Testing
```
ftl-player-v{VERSION}-{SEQUENCE}-{DESCRIPTION}.apk
```

**Examples:**
- `ftl-player-v1.0.0-001-debug.apk`
- `ftl-player-v1.0.0-002-eq-feature.apk`
- `ftl-player-v1.2.1-005-performance-opt.apk`

### Experimental/Feature Testing
```
ftl-player-exp-{SEQUENCE}-{FEATURE}.apk
ftl-player-{FEATURE}-v{VERSION}-{SEQUENCE}.apk
```

**Examples:**
- `ftl-player-exp-001-neural-engine.apk`
- `ftl-player-ai-v0.1.0-001-prototype.apk`
- `ftl-player-biometric-v0.2.0-003-heartrate.apk`

### Component Breakdown

#### Version Format
- **v{MAJOR}.{MINOR}.{PATCH}**: Semantic versioning
- **v1.0.0**: Initial release
- **v1.1.0**: New features added
- **v1.0.1**: Bug fixes only

#### Sequence Numbers
- **001, 002, 003**: Zero-padded 3-digit sequential numbers
- **Reset to 001** for each new version
- **Continue incrementing** within the same version

#### Description Guidelines
- **Keep under 15 characters**
- **Use kebab-case** (lowercase with hyphens)
- **Be descriptive but concise**
- **Common descriptions:**
  - `debug`: Debug build with logging
  - `release`: Release candidate
  - `eq-feature`: Equalizer functionality
  - `ui-update`: User interface changes
  - `performance`: Performance optimizations
  - `bugfix`: Bug fixes
  - `ai-features`: AI/ML functionality
  - `audio-engine`: Core audio improvements

## Build Types and Purposes

### Debug Builds
```bash
# Generate debug APK
./gradlew assembleDebug

# Naming: ftl-player-v1.0.0-001-debug.apk
# Purpose: Development testing, full logging enabled
```

### Release Builds
```bash
# Generate release APK
./gradlew assembleRelease

# Naming: ftl-player-v1.0.0-001-release.apk  
# Purpose: Production-ready testing, optimized
```

### Feature Builds
```bash
# Generate feature-specific build
./gradlew assembleDebug -PfeatureFlag=ai_processor

# Naming: ftl-player-v1.0.0-002-ai-features.apk
# Purpose: Testing specific features
```

## Storage and Organization

### Directory Rules
1. **Create version directories** for each major.minor release
2. **Use experimental/** for prototype builds
3. **Keep maximum 5 APKs per version** (delete oldest when exceeded)
4. **Archive important milestones** to external storage if needed

### File Management
```bash
# Example organization commands
mkdir -p apk-test-packages/v1.0.0
cp app/build/outputs/apk/debug/app-debug.apk \
   apk-test-packages/v1.0.0/ftl-player-v1.0.0-001-debug.apk

# Clean old builds (keep latest 5)
find apk-test-packages/v1.0.0 -name "*.apk" | sort -V | head -n -5 | xargs rm
```

## Testing Workflow

### 1. Pre-Build Checklist
- [ ] Update version in `build.gradle` if needed
- [ ] Determine appropriate sequence number
- [ ] Choose descriptive build name
- [ ] Verify target directory exists

### 2. Build Process
```bash
# Clean previous builds
./gradlew clean

# Build APK
./gradlew assembleDebug  # or assembleRelease

# Copy to test directory with proper naming
cp app/build/outputs/apk/debug/app-debug.apk \
   apk-test-packages/v1.0.0/ftl-player-v1.0.0-003-ui-update.apk
```

### 3. Post-Build Actions
- [ ] Verify APK installs correctly
- [ ] Document any issues or notes
- [ ] Update testing log if maintained
- [ ] Clean build artifacts from `app/build/`

## Quality Assurance

### APK Validation
```bash
# Check APK integrity
aapt dump badging apk-test-packages/v1.0.0/ftl-player-v1.0.0-001-debug.apk

# Verify signing
jarsigner -verify -verbose -certs your-app.apk

# Check file size (should be reasonable)
ls -lh apk-test-packages/v1.0.0/*.apk
```

### Installation Testing
```bash
# Install on device
adb install apk-test-packages/v1.0.0/ftl-player-v1.0.0-001-debug.apk

# Update existing installation  
adb install -r apk-test-packages/v1.0.0/ftl-player-v1.0.0-002-eq-feature.apk

# Uninstall
adb uninstall com.ftl.audioplayer
```

## Documentation Requirements

### Build Notes
For each APK, maintain notes about:
- **Purpose**: What is being tested
- **Changes**: What's new in this build
- **Known issues**: Any bugs or limitations
- **Test results**: Pass/fail status
- **Installation notes**: Special requirements

### Example Build Log
```markdown
## ftl-player-v1.0.0-003-ui-update.apk
- **Date**: 2024-01-15
- **Purpose**: Test new cyberpunk UI theme
- **Changes**: Updated colors, fonts, animations
- **Known Issues**: Equalizer visualization flickers
- **Test Status**: ✅ UI works, ❌ EQ needs fix
- **Next Steps**: Fix EQ visualization in build 004
```

## Automation Scripts

### Build and Name Script
```bash
#!/bin/bash
# build-test-apk.sh

VERSION=$1
SEQUENCE=$2
DESCRIPTION=$3

if [ -z "$VERSION" ] || [ -z "$SEQUENCE" ] || [ -z "$DESCRIPTION" ]; then
    echo "Usage: ./build-test-apk.sh v1.0.0 001 debug"
    exit 1
fi

./gradlew clean assembleDebug

mkdir -p "apk-test-packages/$VERSION"

cp app/build/outputs/apk/debug/app-debug.apk \
   "apk-test-packages/$VERSION/ftl-player-$VERSION-$SEQUENCE-$DESCRIPTION.apk"

echo "Created: apk-test-packages/$VERSION/ftl-player-$VERSION-$SEQUENCE-$DESCRIPTION.apk"
```

## Best Practices

### DO
- ✅ Always use proper naming conventions
- ✅ Store APKs in version-specific directories
- ✅ Keep build artifacts out of git
- ✅ Document each build's purpose
- ✅ Test APKs before distribution
- ✅ Clean up old builds regularly

### DON'T
- ❌ Commit APK files to GitHub
- ❌ Use random or unclear naming
- ❌ Store APKs in source directories
- ❌ Keep unlimited old builds
- ❌ Skip testing before naming/storing
- ❌ Forget to document build changes

## Integration with CI/CD

### GitHub Actions Example
```yaml
# .github/workflows/build-test-apk.yml
name: Build Test APK
on:
  push:
    branches: [feature/*]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build APK
        run: ./gradlew assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v2
        with:
          name: ftl-player-${{ github.sha }}-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

### Common Issues
1. **APK too large**: Check for included debug symbols, unused resources
2. **Installation fails**: Verify signing, check device compatibility
3. **App crashes**: Review logs, check permissions
4. **Performance issues**: Profile with Android Studio tools

### Debug Commands
```bash
# View APK contents
unzip -l ftl-player-v1.0.0-001-debug.apk

# Check permissions
aapt dump permissions ftl-player-v1.0.0-001-debug.apk

# Analyze APK size
./gradlew :app:analyzeDebugBundle
```

---

This guide ensures consistent, professional APK management for the FTL Hi-Res Audio Player project. Update this document as the project evolves and new requirements emerge.