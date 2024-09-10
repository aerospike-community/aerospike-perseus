# Perseus
Perseus is a versatile tool designed to test and demonstrate the core capabilities of Aerospike technology. It can generate a flexible workload encompassing a wide range of operations, including reads, writes, updates, deletes, expressions, batch operations, User Defined Functions (via LUA code running on the server), secondary index queries, and aggregations. Users can dynamically adjust the workload in real time to observe how the server responds and ensure the hardware can handle the load effectively.

This document explains how to use this tool.


## Running Perseus

You don't need to run Perseus locally, as this repository includes automations that allow you to set up Aerospike clusters of any size and run Perseus against them. However, if you wish to run Perseus locally for any reason, here are the prerequisites:

- The codebase is written for JDK 22 and won’t compile with earlier versions.
- Maven is required to build the project, so you’ll need to have Maven installed.
- Perseus requires an Aerospike cluster to connect to. If you don’t have one available for testing, the next section will guide you through setting up a local cluster.

To generate an executable file, navigate to the root folder of the project and run the following command:

```mvn package```

If the build is successful, the executable will be placed in:

```./aerospike-perseus/target/perseus-1.0-SNAPSHOT.jar```


There are 4 files in the src/main/resources. These files control the behaviour of Perseus. 

📦src\main\java\resources\
┣ 📜configuration.yaml

┣ 📜threads.yaml 

┣ 📜same_as_expression.lua 

┗ 📜udf_aggregation.lua 

These files need to be in the correct path when running the executable. You can either copy them next to the executable and configure them before running Perseus, or, for simplicity, you can run the executable once, knowing it will fail. If the files are missing, Perseus will automatically create default versions in the required path. The default configuration assumes the Aerospike cluster is running on localhost, so if your cluster isn’t on localhost, the application will fail initially. However, this will ensure the files are placed in the correct location, allowing you to modify them as needed afterwards.

To run the executable, run the following command:

```/opt/jdk-22/bin/java -jar perseus-1.0-SNAPSHOT-jar-with-dependencies.jar ```

## Configuring Perseus

Perseus configurations are managed through two files: configuration.yaml and threads.yaml.

### configuration.yaml
This file is only checked once at the beginning of the execution:
- aerospikeConfiguration: Details related to the Aerospike cluster that Perseus should connect to and use.
  - hosts: List the IPs and ports of the Aerospike nodes. The IP and port of a single node will suffice.
  - username and password: If Aerospike authentication is enabled, provide the username and password with access to the cluster. If authentication is not required, simply leave these fields empty.
  - namespace: The Aerospike namespace used in the test. 
  - set: the name of the set used in the test.
  - truncateSet: If set to true, Perseus will execute a truncate command on the set before starting the test. Enable this option if you want to run the test on an empty set. Note that after truncation, it may take some time for all the space to become fully available.
  - testConfiguration
    - keyCaching: To read, delete, and modify records in Aerospike, Perseus caches the keys of records that were inserted into the database. This cache can randomly sample data to ensure that queries are not limited to only the most recently inserted records.
      - cacheCapacity: The capacity of the cache determines the diversity of reads on the cluster. A larger cache allows for more varied reads. Keep in mind that the cache is stored in memory, so your machine must have sufficient RAM. Each cache entry takes up 8 bytes, meaning 1 billion entries will require 8 GB of memory.
      - discardRatio: The sampling ratio, a floating point number between 0 and 1. A smaller number results in fewer samples being taken, which means the cache will take longer to fill but will contain more diverse data.
    - recordSize: The average record size that will be inserted into the database. The size of the records generated by Perseus follow a normal distribution, with the mean set to this specified value.
    - readBatchSize: Number of records in a read batch. 
    - writeBatchSize: Number of records in a write batch. 
    - readHitRatio: In some use cases, some requested data do not exist in the database. This parameter defines the percentage of requests that successfully read an existing record. This number is a floating point number between 0 and 1.0 (a value of 0.1 means 10% hit and 90% miss).
    - **NOTE**:Enabling the following four configurations will create secondary indexes. Keep in mind that this will affect the write workload and increase memory usage. If these indexes are unnecessary, it's best to leave them disabled. Additionally, creating an index on a database with existing data can take a considerable amount of time. While the test can proceed during index creation, secondary index query workloads must be disabled to prevent exceptions until the process is complete.
      - stringIndex: If set to True, Perseus will create a new String index on the records and enable String query workload. 
      - numericIndex: If set to True, Perseus will create a new Numeric index on the records and enable Numeric query workload. 
      - geoSpatialIndex: If set to True, Perseus will create a new GeoSpatial index on the records and enable GeoSpatial query workload. 
      - udfAggregation: If set to True, Perseus will create a Numeric index on the records and enable UDF Aggregation workload. UDF Aggregation performs range queries and therefore are quite resource intensive. 
  - outputWindowConfiguration: Details related to the perseus output window. 
    - printIntervalSec: Interval, in seconds, between each output line.
      - numberOfLinesBeforeReprintingTheHeader: The number of output lines after which the header will be printed again.

### threads.yaml

Modifying this file will affect the behaviour of Perseus in real-time, as it is checked at the beginning and every second thereafter. By adjusting the number of threads for each test case, you can dynamically alter the shape of the workload.

The number in front of each line specifies the number of threads that will execute the update workload. Here is a explnation about each workload. 

- **NOTE**: The keys of inserted records in the following workloads are cached in the keyCache, allowing other workloads to access data known to be in the database. You can modify the keyCache specifications in configuration.yaml.
  - write: Perseus generates random records with an average size defined by the recordSize parameter in configuration.yaml. 
  - batchWrite: Similar to the Write workload, but a batch of records is written together. The batch size is defined by writeBatchSize in configuration.yaml.
- **Note**: The following workloads use the records stored in the keyCache to access data that is known to exist in the database.
  - read: Based on the readHitRatio value in configuration.yaml, it generates read requests where a certain percentage are guaranteed to correspond to records that have already been inserted into the database. 
  - update: Update records by adding a new bin to them. 
  - delete: Delete records durably.
  - batchRead: Similar to the Read workload, but a batch of records is read together. The batch size is defined by readBatchSize in configuration.yaml.
- **Note**: The following Expression and UDF workloads perform the same logic in slightly different formats. The logic is: Subtract the value of one bin from another, and if the result equals a specified value, the output is 'Yes'; otherwise, it’s 'No'.
  - expressionRead: Run the expression on the records and return the result as the response. 
  - expressionWrite: Run the expression on the records and insert the result as new bin.
  - udf: This is similar to the expression write workload, but the logic is written in the LUA programming language. You can find the code that gets uploaded to Aerospike by Perseus in same_as_expression.lua.
- **Note**: The following workloads are available only if they have been enabled in configuration.yaml. All of these workloads use significantly more resources and doing many of the at the same time can decrease the throughput of the system.
  - numericSearch: It uses a Numeric secondary index to retrieve a single record.
  - stringSearch: It uses a String secondary index to retrieve a single record.
  - geospatialSearch: It uses a GeoSpatial secondary index to retrieve a single record.
  - udfAggregation: Using the LUA programming language, it calculates an average value across multiple records retrieved through a range query that utilises a numeric secondary index. The source code that Perseus uploads to Aerospike can be found in udf_aggregation.

## Running Aerospike 

If you don't have access to an Aerospike cluster or you don't want to set up one, you can use either of the following two approaches to set one up for use with Perseus.

### Running a single node Aerospike cluster on your local machine. 
While this is the simplest way to test Perseus, the resources available on a single machine are limited, making it unsuitable for testing Aerospike’s performance. Additionally, due to the limited resource allocation, a single-node cluster can quickly go out of space, even with just tens of thousands of write requests per second, potentially filling up within few minutes.
#### Prerequisite 
- Ensure Docker Desktop is installed on your machine to run the following commands: https://www.docker.com/products/docker-desktop/
- The latest version of the Aerospike server image can be found at: https://hub.docker.com/_/aerospike.
- You can test all features of Perseus, except for the delete workload, using the Aerospike Community Edition. Images for the Community Edition include "ce" in their names.
- If you have a feature file for Aerospike Enterprise, you can use the Enterprise Edition. Images for the Enterprise Edition are marked with "ee" in their names.
- You can obtain a temporary License to test the Enterprise Edition here: https://aerospike.com/get-started-aerospike-database/

To set up a one-node Aerospike cluster locally using Docker, run the following command:

```docker run --rm -d --name aerospike -p 3000-3002:3000-3002 aerospike:ce-7.1.0.5```

After running this command, you should have an in-memory Aerospike node running on localhost:3000. The Perseus default configuration file points to this address, so you can simply run the Perseus executable. 

If you want to attach to the node to modify its behaviour, use the following command:

```docker exec -it aerospike bash ```

To view the Aerospike logs, run the following command:

```docker logs aerospike```

If the resources of the single-node cluster are exhausted, the simplest solution is to stop the Docker instance and start a new one. To stop the Docker container, use the following command:

```docker container stop aerospike```

## Creating Aerospike cluster on AWS using Aerolab
<<Under construction>>
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