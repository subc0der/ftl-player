#!/bin/bash

# ╔══════════════════════════════════════════════════════════════════════════════════╗
# ║                    🔐 FTL AUDIO PLAYER BRANCH PROTECTION 🔐                      ║
# ║                      Neural Code Quality Enforcement System                       ║
# ║                                                                                    ║
# ║    🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE 🎵      ║
# ╚══════════════════════════════════════════════════════════════════════════════════╝

# This script configures comprehensive branch protection rules for the FTL Audio Player
# Requires: GitHub CLI (gh) and appropriate repository permissions

set -e

# Configuration
REPO="subc0der/ftl-player"
echo "🎵 FTL Audio Player - Neural Branch Protection Configuration"
echo "════════════════════════════════════════════════════════════"

# Color codes for output
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${CYAN}⚡${NC} $1"
}

print_success() {
    echo -e "${GREEN}✅${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠️${NC} $1"
}

print_error() {
    echo -e "${RED}❌${NC} $1"
}

# Check if GitHub CLI is installed
if ! command -v gh &> /dev/null; then
    print_error "GitHub CLI (gh) is not installed. Please install it first."
    echo "Visit: https://cli.github.com/"
    exit 1
fi

# Check authentication
print_status "Checking GitHub authentication..."
if ! gh auth status &> /dev/null; then
    print_error "Not authenticated with GitHub. Please run: gh auth login"
    exit 1
fi
print_success "GitHub authentication confirmed"

# Set default repository
print_status "Setting default repository to $REPO..."
gh repo set-default "$REPO"
print_success "Default repository set"

# ═══════════════════════════════════════════════════════════════════════════════════
# MAIN/MASTER BRANCH PROTECTION
# ═══════════════════════════════════════════════════════════════════════════════════

print_status "Configuring protection for main/master branch..."

# Check which branch exists (main or master)
MAIN_BRANCH="master"
if gh api "repos/$REPO/branches/main" &> /dev/null; then
    MAIN_BRANCH="main"
fi

print_status "Protecting $MAIN_BRANCH branch..."

gh api \
  --method PUT \
  "repos/$REPO/branches/$MAIN_BRANCH/protection" \
  --input - << EOF
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "🧠 Neural Code Quality Analysis",
      "🎵 Audiophile Unit Tests (API 34)",
      "🎵 Neural Audio APK Generation",
      "🔐 Cyberpunk Security Analysis"
    ]
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "dismissal_restrictions": {},
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": true,
    "required_approving_review_count": 2,
    "require_last_push_approval": true
  },
  "restrictions": null,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "block_creations": false,
  "required_conversation_resolution": true,
  "lock_branch": false,
  "allow_fork_syncing": false,
  "required_linear_history": false,
  "required_signatures": false
}
EOF

if [ $? -eq 0 ]; then
    print_success "$MAIN_BRANCH branch protection configured successfully"
else
    print_warning "$MAIN_BRANCH branch protection may have failed (might already exist)"
fi

# ═══════════════════════════════════════════════════════════════════════════════════
# DEVELOP BRANCH PROTECTION
# ═══════════════════════════════════════════════════════════════════════════════════

print_status "Configuring protection for develop branch..."

gh api \
  --method PUT \
  "repos/$REPO/branches/develop/protection" \
  --input - << EOF
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "🧠 Neural Code Quality Analysis",
      "🎵 Audiophile Unit Tests (API 34)",
      "🎵 Neural Audio APK Generation"
    ]
  },
  "enforce_admins": false,
  "required_pull_request_reviews": {
    "dismissal_restrictions": {},
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": false,
    "required_approving_review_count": 1,
    "require_last_push_approval": false
  },
  "restrictions": null,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "block_creations": false,
  "required_conversation_resolution": true,
  "lock_branch": false,
  "allow_fork_syncing": false,
  "required_linear_history": false,
  "required_signatures": false
}
EOF

if [ $? -eq 0 ]; then
    print_success "develop branch protection configured successfully"
else
    print_warning "develop branch protection may have failed (might already exist)"
fi

# ═══════════════════════════════════════════════════════════════════════════════════
# ADDITIONAL BRANCH PROTECTION RULES
# ═══════════════════════════════════════════════════════════════════════════════════

# Release branches protection (if they exist)
print_status "Checking for release branches..."
RELEASE_BRANCHES=$(gh api "repos/$REPO/branches" --jq '.[].name' | grep '^release/' || true)

if [ -n "$RELEASE_BRANCHES" ]; then
    for branch in $RELEASE_BRANCHES; do
        print_status "Protecting $branch..."
        
        gh api \
          --method PUT \
          "repos/$REPO/branches/$branch/protection" \
          --input - << EOF
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "🧠 Neural Code Quality Analysis",
      "🎵 Audiophile Unit Tests (API 34)",
      "🔐 Cyberpunk Security Analysis"
    ]
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "dismissal_restrictions": {},
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": true,
    "required_approving_review_count": 2,
    "require_last_push_approval": true
  },
  "restrictions": null,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "required_conversation_resolution": true
}
EOF
        
        if [ $? -eq 0 ]; then
            print_success "$branch protection configured"
        else
            print_warning "$branch protection may have failed"
        fi
    done
else
    print_status "No release branches found"
fi

# ═══════════════════════════════════════════════════════════════════════════════════
# CONFIGURE AUTO-DELETE HEAD BRANCHES
# ═══════════════════════════════════════════════════════════════════════════════════

print_status "Configuring auto-delete head branches after merge..."
gh api \
  --method PATCH \
  "repos/$REPO" \
  --field delete_branch_on_merge=true

if [ $? -eq 0 ]; then
    print_success "Auto-delete head branches enabled"
else
    print_warning "Auto-delete configuration may have failed"
fi

# ═══════════════════════════════════════════════════════════════════════════════════
# CONFIGURE MERGE SETTINGS
# ═══════════════════════════════════════════════════════════════════════════════════

print_status "Configuring merge settings..."
gh api \
  --method PATCH \
  "repos/$REPO" \
  --field allow_squash_merge=true \
  --field allow_merge_commit=true \
  --field allow_rebase_merge=false \
  --field allow_auto_merge=true

if [ $? -eq 0 ]; then
    print_success "Merge settings configured"
else
    print_warning "Merge settings configuration may have failed"
fi

# ═══════════════════════════════════════════════════════════════════════════════════
# SUMMARY
# ═══════════════════════════════════════════════════════════════════════════════════

echo ""
echo "╔════════════════════════════════════════════════════════════════════════════╗"
echo "║                  🔐 BRANCH PROTECTION CONFIGURATION COMPLETE 🔐              ║"
echo "╚════════════════════════════════════════════════════════════════════════════╝"
echo ""
echo "🎵 FTL Audio Player Branch Protection Summary:"
echo ""
echo "  ${GREEN}✅${NC} $MAIN_BRANCH branch:"
echo "     • 2 required reviews with code owner approval"
echo "     • Dismiss stale reviews enabled"
echo "     • Require branches to be up to date"
echo "     • Administrators included in restrictions"
echo "     • Required status checks must pass"
echo "     • Require conversation resolution"
echo ""
echo "  ${GREEN}✅${NC} develop branch:"
echo "     • 1 required review"
echo "     • Dismiss stale reviews enabled"
echo "     • Require branches to be up to date"
echo "     • Required status checks must pass"
echo "     • Administrators can bypass (for hotfixes)"
echo ""
echo "  ${GREEN}✅${NC} Additional configurations:"
echo "     • Auto-delete head branches after merge"
echo "     • Squash and merge commit allowed"
echo "     • Rebase merge disabled (maintain history)"
echo "     • Auto-merge enabled for approved PRs"
echo ""
echo "⚡ Neural audio code quality enforcement active!"
echo "🎵 Cyberpunk development workflow protected!"
echo "🤖 AI-assisted development standards enforced!"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════════
# OPTIONAL: CREATE CODEOWNERS FILE
# ═══════════════════════════════════════════════════════════════════════════════════

print_status "Creating CODEOWNERS file..."
cat > .github/CODEOWNERS << 'EOL'
# FTL Audio Player - Neural Code Ownership Matrix
# ═══════════════════════════════════════════════════════════════════
# 🎵 CYBER AQUA (#00FFFF) • NEURAL INDIGO (#4B0082) • AUDIOPHILE GRADE
# ═══════════════════════════════════════════════════════════════════

# Default owner for everything
* @subc0der

# Audio Engine (C++ DSP, Native Processing)
/audio-engine/ @subc0der
/app/src/main/cpp/ @subc0der
*.cpp @subc0der
*.h @subc0der

# Neural AI Components (TensorFlow Lite, ML Models)
/ml-models/ @subc0der
/app/src/main/kotlin/com/ftl/audioplayer/ai/ @subc0der
*NeuralProcessor.kt @subc0der
*AI*.kt @subc0der

# 32-Band Parametric EQ
/app/src/main/kotlin/com/ftl/audioplayer/audio/eq/ @subc0der
*Equalizer*.kt @subc0der
*EQ*.kt @subc0der

# Cyberpunk UI/UX
/app/src/main/kotlin/com/ftl/audioplayer/ui/ @subc0der
/app/src/main/res/ @subc0der
*Theme*.kt @subc0der
*.xml @subc0der

# Fitness & Biometric Integration
/app/src/main/kotlin/com/ftl/audioplayer/fitness/ @subc0der
*Workout*.kt @subc0der
*Biometric*.kt @subc0der

# Settings System (200+ options)
/app/src/main/kotlin/com/ftl/audioplayer/settings/ @subc0der
*Settings*.kt @subc0der

# Documentation
/docs/ @subc0der
*.md @subc0der
/CLAUDE.md @subc0der

# CI/CD and GitHub Actions
/.github/ @subc0der
*.yml @subc0der
*.yaml @subc0der

# Build Configuration
*.gradle @subc0der
*.gradle.kts @subc0der
/gradle/ @subc0der
EOL

if [ $? -eq 0 ]; then
    print_success "CODEOWNERS file created"
else
    print_warning "CODEOWNERS file creation may have failed"
fi

echo ""
echo "🎵 Branch protection configuration complete!"
echo "⚡ Your neural audio codebase is now protected with audiophile-grade quality gates!"