#!/system/xbin/bash

STATUS=`adb devices | tail -2 | head -1 | awk {'print $2'} | tr -d "\n"`
if [ "$STATUS" == "device" ]; then
        echo ENABLED
else
        echo DISABLED
fi
