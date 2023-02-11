# Perseus
Perseus is a tool for demonstrating some of the core capabilities of Aerospike technology. Perseus can generate a high number of transactions per second of varied types, such as reads, writes, updates, expressions, batches, User Defined Functions (through LUA code running on the server), secondary index queries, and aggregations. The user can dynamically change the load of each transaction type to see how the server reacts to different shapes of throughputs.

This tool was tested against a 4-node aerospike cluster on i4i.4xlarge EC2 instances. The cluster handled 500+K transactions per second while managing around 3 TB of data (the size before replication).

This document explains how to use this tool.

Note: This codebase is written for JDK 18 and won’t compile on lower versions!

## How to use the tool locally

To run the application, you need to have an aerospike cluster.

### The Aerospike cluster
You can build one in the cloud or run one locally. To set up a one-node cluster locally using docker, run:

```docker run --rm -d --name aerospike -p 3000-3002:3000-3002 aerospike:ce-6.2.0.2```

You should be able to attach the node by running the following:

```docker exec -it aerospike bash ```

You can look at the aerospike logs by running the following:

```docker logs aerospike```

Note: After running the tool for a while, the instance resources can be exhausted; therefore, the application may start to get out of disk/memory exceptions. If that happens, you need to connect to the cluster and do a cleanup (perhaps truncate the namespace). If it’s a docker instance, you may stop the container with the following command and then rerun a fresh instance.

```docker container stop aerospike```

### Running the application 
There are 3 files in the src/main/resources that control the behaviour of Perseus.

📦src\main\java\resources\
┣ 📜configuration.yaml\
┣ 📜example.lua\
┗ 📜threads.yaml

These 3 files are copied to the working directory at runtime if they are not already there. The application will use the files in the working directory. This would allow the user to change the configurations easily after the first run.

To run the application in an IDE, run the Main method of Class:

```com.aerospike.perseus.Main```

To produce a Fat Jar that can be run without an IDE, run the following command at the root of the project:

```mvn package```

If the build is successful, the UberJar will be located in the following:

```./aerospike-perseus/target/perseus-1.0-SNAPSHOT.jar```

To run the jar, you need JDK 18. You can run the jar with this command.

```/opt/jdk-18/bin/java -jar perseus-1.0-SNAPSHOT-jar-with-dependencies.jar ```

The first time you run the application, configuration.yaml, threads.yaml, and lua/example.lua will be created in the working directory. After that, you can modify them to change the behaviour of Perseus.

## Configuring Perseus
### configuration.yaml
This file is only checked once at the beginning of the execution:
- Hosts: The IP and Port of the Aerospike nodes.
- Namespace: The Aerospike namespace. (Note that the default “test” namespace is an in-memory namespace. If you want a persistent one, you need to modify the aerospike.conf)
- SetName: the name of the Set.
- AutoDiscardingKeyCapacity: The number of samples kept in the memory for running read and update queries.
- AutoDiscardingKeyRatio: The ratio of the write requests which should be kept as a sample for running the read and the update queries.
- PrintIntervalSec: The interval that the results are printed out.
- NumberOfLinesToReprintHeader: The number of lines that the header will be reprinted.
- ThreadYamlFilePath: the location of the thread.yaml relative to the working directory.
- LuaFilePath: location of the example.lua relative to the working directory.
- SizeOfTheDummyPartOfTheRecord: The size of the dummy part of the record. Modify to make the size of records smaller or larger.

### threads.yaml
This file is checked every 1 second. You can change the number of threads the client uses for each test case. The output would reflect the number of operations performed using the number of threads specified.
- Write: Number of the writer threads. Each write is a random record generated in com.aerospike.perseus.data.provider.random.SimpleRecordProvider
- Read: Number of the reader threads. This test case reads the records that the writer threads have written since the beginning of the execution on a random basis.
- Update: Number of update threads. This test case updates the records that the writer threads have written since the beginning of the execution on a random basis.
- ExpRead: The number of threads that run a simple operation using expressions on a single record and return the result.
- ExpWrite: The number of threads that run a simple operation using expressions on a single record and persist the result on the same record.
- UDF: The number of threads that run a simple operation using UDF (LUA) on a single record and return the result.
- Search: The number of threads that use a secondary index to retrieve a unique record.
- UDFAgg: The number of threads that use a secondary index to retrieve a group of records, aggregate them, and return the result.

Note: because updates, reads, SI queries, expressions, and UDF all depend on the samples stored in the AutoDiscardingList, and the list only being populated by writes, you always need to have some writer threads writing a record at the beginning until some samples are stored in the memory of the app. Running the application with 0 writer threads from the beginning won't yield a meaningful result.

### example.lua
The Lua code will be used to run the UDF-related test cases.

## How to demo Perseus
This repository also has the scripts to set up an Aerospike cluster on AWS, configure the Aerospike monitoring stack, and run the Perseus in a single command.

To use these scripts, you must install and configure Aerolab on your machine: https://github.com/aerospike/aerolab. Follow the https://github.com/aerospike/aerolab/blob/master/docs/GETTING_STARTED.md guide to install and configure aws-cli.

After the setup is complete, open a terminal and cd to the aerolab directory in this repository. Open the configure.sh file. The options are self-explanatory. You don't need to change anything except SEC_GROUP and SUBNET. Modify these two values based on your AWS account.

HINT: Use the default subnet and Security Group of the region you want to use.

NOTE: The Security Group should allow ALL TRAFFIC inside the security group, SSH from 0.0.0.0/0, and Custom TCP from 3000 on 0.0.0.0/0.

Now run:
```./setup.sh```

NOTE: *Very important* to remember to run ```./destroy.sh``` soon. This demo uses relatively expensive hardware. Running it for a few days can easily cost hundreds of dollars. 

If everything runs successfully, the script will open a grafana dashboard (user: admin, password: admin) that shows the state of the cluster, and in the terminal, you should see something like this:
```
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Seconds      |  Writes (2)   |  Reads (2)    |  Updates (0)  |  Exp R (0)    |  Exp W (0)    |  Batch W (0)  |  Searches (0) |  LUAs (0)     |  LUA Aggs (0) |  Total        |
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1            |  1985         |  2075         |  0            |  0            |  0            |  0            |  0            |  0            |  0            |  4060         |
2            |  2271         |  2353         |  0            |  0            |  0            |  0            |  0            |  0            |  0            |  4624         |
3            |  2116         |  2178         |  0            |  0            |  0            |  0            |  0            |  0            |  0            |  4294         |
```

Now, in another terminal, run:
```./connect_client.sh```

Now, you are connected to the node that runs Perseus. you can pen thread.yaml (in /root) and change the number of threads as described in the previous sections. You can see the result in the other terminal and grafana.

Don't forget to run ```./destroy.sh``` when you are done. Jeff doesn't need more money.