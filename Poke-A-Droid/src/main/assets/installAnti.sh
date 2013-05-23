#!/system/xbin/bash
# p2p-adb

cd "$(dirname "$0")"

adb install AntiGuard.apk >/dev/null 2>&1
NOOUTPUT=$(adb shell am start io.kos.antiguard/.unlock)

echo $?