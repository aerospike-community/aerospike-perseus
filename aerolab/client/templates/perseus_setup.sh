#!/bin/bash
sudo apt update
sudo apt install -y openjdk-22-jdk
sudo apt install -y maven

cd /root
git clone https://github.com/behrockz/aerospike-perseus
cd /root/aerospike-perseus
mvn package

echo "Setup of Perseus is finished!"