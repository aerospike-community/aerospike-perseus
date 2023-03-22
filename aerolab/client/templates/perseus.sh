#!/bin/bash
/opt/jdk-18/bin/java -jar /root/aerospike-perseus/target/perseus-1.0-SNAPSHOT-jar-with-dependencies.jar &> /root/out.log &
disown -r