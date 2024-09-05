#!/bin/bash
PID=$(ps aux | grep '[a]erospike-perseus' | awk '{print $2}')
if [ ! -z "$PID" ]
then
    echo "Kill the old process!"
    sudo kill -9 $PID
fi

java -Xmx31g -jar /root/aerospike-perseus/target/perseus-1.0-SNAPSHOT-jar-with-dependencies.jar &> /root/out.log &
disown -r