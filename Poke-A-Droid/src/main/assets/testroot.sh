#!/system/xbin/bash

WHOAMI=$(adb shell 'id' | tr -d "\r"  )
TRYROOT=$(adb shell 'su -c "id"' | tr -d "\r" )

if echo $WHOAMI | grep 'uid=0' 2>&1 >/dev/null
then
        echo "Running as root"
elif echo $TRYROOT | grep 'uid=0' 2>&1 >/dev/null
then
        echo "Not natively root."
        echo "Will continue to escalate with su."
elif echo $WHOAMI | grep 'uid=2000' 2>&1 >/dev/null
then
        echo "Running as shell"
else
        echo "Unknown state"
fi
