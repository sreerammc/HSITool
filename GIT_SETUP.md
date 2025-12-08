# Git Setup Instructions

This guide will help you commit and push the blobutil project to GitHub.

## Prerequisites

1. **Install Git** (if not already installed):
   - Download from: https://git-scm.com/download/win
   - Or install via: `winget install Git.Git`

2. **Verify Git installation**:
   ```powershell
   git --version
   ```

## Setup Steps

### 1. Initialize Git Repository

Open PowerShell in the project directory (`C:\Dev\blobutil`) and run:

```powershell
git init
```

### 2. Configure Git (if not already configured)

```powershell
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 3. Add Remote Repository

```powershell
git remote add origin https://github.com/sreerammc/HSITool.git
```

### 4. Add All Files

```powershell
git add .
```

### 5. Create Initial Commit

```powershell
git commit -m "Initial commit: JsonQueryUtility with CSV support and memory optimizations"
```

### 6. Push to GitHub

**Option A: If the repository is empty (recommended for first push):**
```powershell
git branch -M main
git push -u origin main
```

**Option B: If the repository has content:**
```powershell
git pull origin main --allow-unrelated-histories
git push -u origin main
```

## Authentication

GitHub may require authentication. You have two options:

### Option 1: Personal Access Token (Recommended)

1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate a new token with `repo` scope
3. When prompted for password, use the token instead

### Option 2: GitHub CLI

```powershell
# Install GitHub CLI
winget install GitHub.cli

# Authenticate
gh auth login
```

## Files Included

The following files will be committed:
- Source code (`src/`)
- Maven configuration (`pom.xml`)
- Documentation (`README.md`, `USAGE.md`)
- `.gitignore` file

The following files are excluded (via `.gitignore`):
- Build artifacts (`target/`)
- Compiled classes (`*.class`)
- User-generated output files
- IDE configuration files

## Verify

After pushing, verify the repository at:
https://github.com/sreerammc/HSITool


