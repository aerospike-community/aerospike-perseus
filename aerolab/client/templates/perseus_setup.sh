#!/bin/bash
wget https://download.java.net/java/GA/jdk18.0.2/f6ad4b4450fd4d298113270ec84f30ee/9/GPL/openjdk-18.0.2_linux-aarch64_bin.tar.gz
tar -xvf openjdk-18.0.2_linux-aarch64_bin.tar.gz
sudo mv jdk-18.0.2 /opt/
export JAVA_HOME=/opt/jdk-18.0.2
export PATH=$PATH:$JAVA_HOME/bin
wget https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz -P /tmp
sudo tar xf /tmp/apache-maven-*.tar.gz -C /opt
sudo ln -s /opt/apache-maven-3.8.8 /opt/maven
PATH="/opt/maven/bin:$PATH"
echo "export PATH=$PATH:/opt/jdk-18/bin:/opt/maven/bin" >> ~/.bashrc

cd /root
git clone https://github.com/behrockz/aerospike-perseus
cd /root/aerospike-perseus
mvn package

echo "Setup of Perseus is finished!"