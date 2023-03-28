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
- BatchSize: The number of records in each batch write transaction.
- ReadHitRatio: In real world scenarios, not all the reads requests return an existing records. This parameter defines the percentage of the requests that reads an existing record (.1 would mean 10% hit, 90% miss). The implementation is just an attempt to reach the suggested ratio, in practice the ratio of successful reads is usually a bit higher than this ratio.   

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
- BatchW: The number of threads that send batch write queries. The number of record in each batch is defined by BatchSize in configuration.yaml.

Note: because updates, reads, SI queries, expressions, and UDF all depend on the samples stored in the AutoDiscardingList, and the list only being populated by writes, you always need to have some writer threads writing a record at the beginning until some samples are stored in the memory of the app. Running the application with 0 writer threads from the beginning won't yield a meaningful result.

### example.lua
The Lua code will be used to run the UDF-related test cases.

## How to Run Perseus at Scale
This repository also has the scripts to set up an Aerospike cluster on AWS, configure the Aerospike monitoring stack, and run the Perseus in a single command.

To use these scripts, you must install and configure Aerolab on your machine: https://github.com/aerospike/aerolab. Follow the https://github.com/aerospike/aerolab/blob/master/docs/GETTING_STARTED.md guide to install and configure aws-cli.

After the setup is complete, open a terminal and cd to the Aerolab directory in this repository. 

This demo requires Aerospike Enterprise License. You can get a 60-day license for free from here: https://aerospike.com/get-started-aerospike-database/. If you need your dev license to get extended, please contact Aerospike. We will be more than happy to help you. 

Configurations of the demo are in configure.sh file. You must change the 'FEATURES' to the address of the license key you downloaded from our website. The rest of the configurations in this file define the specifications of the demo. They are self-explanatory.

Now run:
```./setup.sh```

NOTE: *Very important* You MUST remember to run ```./destroy.sh``` soon. This demo uses relatively expensive hardware. Running the demo costs roughly $7 per hour. Forgetting to release the resources can cost hundreds of dollars. 

The script first creates a cluster by running ```./cluster/setup.sh```.
Then it creates the requested number of clients by running ```./client/setup.sh```. This script open new terminals that tail the result of the demo. (You must have iTerm installed.)
Then it creates the Grafana dashboard by running ```./grafana.setup.sh```, and open a window to its interface (user: admin, password: admin). 
If any of the steps above fail, you can run the scripts individually.

If everything runs successfully, the terminals should show you something like this:
```
=================================================================================================================================================================================
| Collected Keys Stat [Max: 5000000, Save Ratio: 10%, # Processed: 6461, # Collected: 618, Full Percent: 0%, Duration: 00:00:00]                                                |
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| Task          | Write         | Read(Hit %50) | Update        | Expression R  | Expression W  | Batch W (10)  | Search        | UDF           | UDF Aggregate | Total TPS     |
| Thread        | 10 (10)       | 0 (0)         | 0 (0)         | 0 (0)         | 0 (0)         | 0 (0)         | 0 (0)         | 0 (0)         | 0 (0)         |               |
=================================================================================================================================================================================
| 1             | 6711          | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 6711          |
| 2             | 7702          | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 7702          |
| 3             | 6567          | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 6567          |
| 4             | 5721          | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 5721          |
| 5             | 9420          | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 0             | 9420          |
```

NOTE: Perseus (the load tester app) runs in the background, if you accidentally Ctrl-C, it will continue running in the background. You can tail the nohup file to monitor the output again. 

In another terminal open ```./threads.yaml``` and enter the number of threads for each test case. Then run ```./copyThreads.sh``` to transfer the local file to all the clients.

If you want to connect to any of the clients, run ```./connect_client.sh i``` where 'i' is the number of the client you want to connect to.

If you want to add a new node, run ```./cluster/addNode.sh```. 

If you want to stop the Aerospike process on a node, run ```./cluster/stopNode.sh i``` where 'i' is the number of the node you want to be stopped.

If you want to restart the Aerospike process on a node, run ```./cluster/restartNode.sh i``` where 'i' is the number of the node you want to be restarted.

Don't forget to run ```./destroy.sh``` when you are done. Jeff doesn't need more money.

You can also run either iof these if you want to bring down and redeploy parts of the demo:
 - ```./cluster/destroy.sh```
 - ```./client/destroy.sh```
 - ```./grafana/destroy.sh```