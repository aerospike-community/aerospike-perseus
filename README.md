# Perseus
Perseus is a tool for demonstrating some of the core capabilities of Aerospike technology. Perseus can generate a high number of transactions per second of varied types, such as reads, writes, updates, expressions, User Defined Functions (through LUA code running on the server), secondary index queries, and aggregations. The user can dynamically change the load of each transaction type to see how the server reacts to different shapes of throughputs. 

This tool was tested against a 4-node aerospike cluster on i4i.4xlarge EC2 instances. The cluster handled 400+K transactions per second while managing around 3 TB of data (the size before replication). 

This document explains how to use this tool.

Note: This code base is written for JDK 18 and won’t compile on lower versions!

## How to Use

To run the application, you need to have an aerospike cluster. 

### The Aerospike Cluster
You can build one in the cloud or run one locally. To set up a one-node cluster locally using docker, run: 

```docker run --rm -d --name aerospike -p 3000-3002:3000-3002 aerospike:ce-6.2.0.2```

You should be able to attach the node by running the following:

```docker exec -it aerospike bash ```

You can look at the aerospike logs by running the following:

```docker logs aerospike```

Note: After running the tool for a while, the instance resources can be exhausted; therefore, the application may start to get out of disk/memory exceptions. If that happens, you need to connect to the cluster and do a cleanup (perhaps truncate the namespace). If it’s a docker instance, you may stop the container with the following command and then rerun a fresh instance. 

```docker container stop aerospike```

### Running the Appplication
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
The Lua code that will be used to run the UDF related test cases.
