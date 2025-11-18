# AI Usage Declaration

**Project:** Weather App (PROG7314 Final POE)  
**Student:** Shaquille Sookanlall  
**Student Number:** ST10140587  
**Date:** November 18, 2025

---

## AI Tools Used

During the development of this weather application, I utilized the following AI tools:
- **Claude AI (Anthropic)** - For debugging assistance, code explanations, and documentation
- **GitHub Copilot** - For code completion and boilerplate generation

## Manner of AI Usage

AI tools were used as collaborative development partners to enhance my workflow, troubleshoot issues, and improve code quality. Rather than using AI to write code for me, I worked alongside it to understand problems, discuss solutions, and make informed implementation decisions.

### Primary Use Case: ProGuard Configuration Debugging

The most significant use of AI occurred when debugging a critical release build issue. My application worked perfectly in debug mode, but when I generated a release APK, the search functionality completely failed, displaying only "Error" messages.

**Initial Problem:**
I uploaded my `build.gradle.kts` file and empty `proguard-rules.pro` file to Claude AI, explaining that search worked locally but not in the release APK.

**AI-Assisted Analysis:**
Claude AI explained that my empty ProGuard rules file was causing R8 (Android's code shrinker) to obfuscate my data model classes during release builds. The AI walked me through understanding how:
1. R8 renames classes and fields to reduce APK size (e.g., `ForecastResponse` becomes `a`, field `current` becomes `b`)
2. This breaks JSON parsing because Gson can't map JSON field names to the renamed class fields
3. ProGuard rules are needed to tell R8 which classes to keep intact

**Collaborative Solution:**
Based on this explanation, Claude AI generated a comprehensive `proguard-rules.pro` file with detailed comments explaining each rule. I reviewed the generated rules, understood why each was necessary (keeping Gson classes, Retrofit interfaces, Room entities, etc.), and verified they solved my issue by rebuilding and testing the release APK.

**Code Snippet Example:**
```proguard
# Keep ALL data model classes (for JSON parsing)
-keep class com.st10140587.prog7314_poe.data.model.** { *; }

# Keep fields with @SerializedName
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
```

This debugging session taught me about the fundamental differences between debug and release builds, how code obfuscation works, and why proper ProGuard configuration is critical for production apps.

### Additional AI Usage

**Code Completion (GitHub Copilot):**
- ViewBinding initialization patterns
- RecyclerView adapter boilerplate
- Standard try-catch blocks for coroutines

**Documentation (Claude AI):**
- README.md formatting and structure
- Installation instructions organization
- Markdown syntax and badge creation

**Learning Support:**
- Explanations of Kotlin coroutines and Flow
- Room Database setup and DAO patterns
- Firebase Authentication integration concepts

## What AI Did Not Do

AI did not make architectural decisions, design features, write core application logic, or implement the business rules of my weather app. All feature planning, UI/UX design, data flow architecture, and testing strategies were my own work.

## Declaration

I declare that all core code was written by me with full understanding. AI was used to enhance productivity and learning, similar to how professional developers use modern tools like GitHub Copilot. I can explain and recreate any aspect of this project independently.

---

**Student Signature:** Shaquille Sookanlall  
**Student Number:** ST10140587

**Word Count:** 498 words
