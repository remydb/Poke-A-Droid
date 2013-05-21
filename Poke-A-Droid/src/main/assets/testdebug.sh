#!/system/xbin/bash

su -c 'adb kill-server' > /dev/null 2>&1
su -c 'adb start-server' > /dev/null 2>&1

STATUS=`adb devices | tail -2 | head -1 | awk {'print $2'} | tr -d "\n"`
if [ "$STATUS" == "device" ] || [ "$STATUS" == "recovery" ]; then
        echo ENABLED
else
        echo DISABLED
fi

