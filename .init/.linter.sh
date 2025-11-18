#!/bin/bash
cd /home/kavia/workspace/code-generation/android-tv-home-screen-ui-design-265926-265961/android_tv_home_screen_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

