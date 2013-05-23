#!/system/xbin/bash

RMKEY=$(adb shell 'rm /data/system/*.key')

if [ $? == 0 ]; then
    echo 0
else
    echo -1
fi