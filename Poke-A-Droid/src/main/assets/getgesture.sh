#!/system/xbin/bash

cd "$(dirname "$0")"

adb pull /data/system/gesture.key ./gesture.key

KEY=`hexdump -C gesture.key | awk '{print $2 $3 $4 $5 $6 $7 $8 $9 $10 $11 $12 $13 $14 $15 $16 $17}' | tr -d " " | cut -f1 -d "|"`
NOSPACES=`echo ${KEY} | tr -d " "`

zcat AndroidGestureSHA1.txt.gz | grep -i ${NOSPACES} | cut -f1 -d ";"