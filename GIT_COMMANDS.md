# Git Commands to Push to GitHub

Run these commands in PowerShell from the `C:\Dev\blobutil` directory:

## Step 1: Configure Git (if not already done)

Replace with your actual name and email:

```powershell
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

Or for this repository only (without --global):

```powershell
git config user.name "Your Name"
git config user.email "your.email@example.com"
```

## Step 2: Commit the Changes

```powershell
git commit -m "Initial commit: JsonQueryUtility with CSV support, memory optimizations, and batch processing"
```

## Step 3: Rename Branch to Main (if needed)

```powershell
git branch -M main
```

## Step 4: Push to GitHub

```powershell
git push -u origin main
```

**Note:** When you run `git push`, GitHub will prompt you for authentication. You'll need to:
- Use your GitHub username
- Use a Personal Access Token (not your password) as the password

### To create a Personal Access Token:
1. Go to GitHub.com → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token (classic)"
3. Give it a name (e.g., "HSITool Push")
4. Select the `repo` scope
5. Click "Generate token"
6. Copy the token and use it as your password when pushing

---

## Alternative: All Commands at Once

If you've already configured git user.name and user.email, you can run:

```powershell
git commit -m "Initial commit: JsonQueryUtility with CSV support, memory optimizations, and batch processing"
git branch -M main
git push -u origin main
```


