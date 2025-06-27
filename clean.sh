#!/bin/bash
echo "Performing deep clean..."

# Stop all Gradle daemons
pkill -f gradle

# Clean project
rm -rf app/build
rm -rf build
rm -rf .gradle
rm -rf .idea/modules*
rm -rf .idea/libraries
rm -rf ~/.gradle/caches/transforms-*
rm -rf ~/.gradle/caches/modules-*/files-*/com.google.devtools.ksp
rm -rf ~/.kotlin

# Clear Android Studio caches
rm -rf ~/Library/Caches/Google/AndroidStudio*

echo "Clean complete. Please restart Android Studio."
