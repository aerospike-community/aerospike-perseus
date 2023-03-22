#!/bin/bash
wget https://download.java.net/java/GA/jdk18/43f95e8614114aeaa8e8a5fcf20a682d/36/GPL/openjdk-18_linux-x64_bin.tar.gz
tar -xvf openjdk-18_linux-x64_bin.tar.gz
sudo mv jdk-18* /opt/
export JAVA_HOME=/opt/jdk-18
export PATH=$PATH:$JAVA_HOME/bin
wget https://dlcdn.apache.org/maven/maven-3/3.9.0/binaries/apache-maven-3.9.0-bin.tar.gz -P /tmp
sudo tar xf /tmp/apache-maven-*.tar.gz -C /opt
sudo ln -s /opt/apache-maven-3.9.0 /opt/maven
PATH="/opt/maven/bin:$PATH"
echo "export PATH=$PATH:/opt/jdk-18/bin:/opt/maven/bin" >> ~/.bashrc

cd /root
git clone https://github.com/behrockz/aerospike-perseus
cd /root/aerospike-perseus
mvn package

echo "Setup of Perseus is finished!"