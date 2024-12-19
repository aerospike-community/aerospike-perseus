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
This repository also includes scripts that make it a comprehensive toolkit to streamline your Aerospike Proof of Technology! Using these scripts you can easily:
Set up an Aerospike cluster of any size on AWS.
Configure the Aerospike monitoring stack to monitor the cluster.
Run multiple instances of Perseus to create any load on the cluster.
Scale the Aerospike cluster up or down.
Kill a node.

To use these scripts, you’ll need to install and configure Aerolab on your machine. You can find the Aerolab repository here: Aerolab on GitHub.
Follow the instructions in the Getting Started Guide to install Aerolab.
To enable Aerolab to work with AWS, set up the AWS CLI and configure Aerolab for AWS integration by following the steps outlined in the AWS Setup Guide.
Once configured, you’ll be ready to use the scripts for your Aerospike deployment!
These scripts are designed with the assumption that you have access to an Aerospike Enterprise license. If you don’t have one, you can apply for a free 60-day trial license here: Get Started with Aerospike.

The root of the project includes a folder named aerolab, which contains four subfolders:

┣ 📜aws

┣ 📜client

┣ 📜cluster

┗ 📜grafana

The client, cluster, and grafana folders contain scripts related to Aerolab. Unless you’re planning to customise something specific, there’s no need to modify these.
Our focus will primarily be on the contents of the aws folder.

### configure.sh
This is the only file you need to modify to control the cluster size or specify the workload you want to run against it.

- `AWS_REGION`: Specifies the AWS region to use.
- `AWS_EXPIRE`: Length of life of nodes prior to expiry; seconds, minutes, hours, ex 20h 30m. 0 for no expiry.
- `VER`: Aerospike server version.
- `NAMESPACE_NAME`: The name of the namespace Perseus should use. (If you modify this, update the namespace configuration in aerospike.conf.)
- `NAMESPACE_DEFAULT_TTL`: Default TTL (time-to-live) for all records inserted into the database. Set to 0 to disable TTL.
- `NAMESPACE_STORAGE_TYPE`: Choose between HMA or MEMORY. (For HMA, NVMe devices must be attached to the machine.)
- `NAMESPACE_REPLICATION_FACTOR`: The replication factor of the namespace.
- `CLUSTER_NAME`: Name of the Aerospike cluster.
- `CLUSTER_NUMBER_OF_NODES`: Number of Aerospike nodes in the cluster.
- `CLUSTER_INSTANCE_TYPE`: AWS instance type for the cluster.
- `CLUSTER_INSTANCE_NUMBER_OF_NVMES`: Number of NVMe devices attached to each machine.
- `CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME`: Number of partitions per NVMe device.
- `CLUSTER_OVERPROVISIONING_PERCENTAGE`: Percentage of overprovisioning required. (AWS recommends overprovisioning for some instance types. Overprovisioning might not be necessary for light workloads.)
- `GRAFANA_NAME`: Name of the Grafana instance.
- `GRAFANA_INSTANCE_TYPE`: AWS instance type for Grafana.
- `CLIENT_NAME`: Name of the instance running Perseus.
- `CLIENT_INSTANCE_TYPE`: Instance type for Perseus. (The machine requires enough RAM to cache inserted records and sufficient CPUs to handle queries. No disk is needed; the C6a family is a good choice.)
- `CLIENT_NUMBER_OF_NODES`: Number of Perseus nodes. (For heavy workloads, running multiple Perseus instances can provide additional resources and network bandwidth.)
- `STRING_INDEX`: Enables a secondary index of type string. (Disabling this will disable string search workloads. Note: Enabling this impacts write throughput.)
- `NUMERIC_INDEX`: Enables a secondary index of type numeric. (Disabling this will disable numeric search workloads. Note: Enabling this impacts write throughput.)
- `GEO_SPATIAL_INDEX`: Enables a secondary index of type geo-spatial. (Disabling this will disable geo-spatial search workloads. Note: Enabling this impacts write throughput.)
- `UDF_AGGREGATION`: Enables UDF aggregation workloads. (Disabling this will disable the associated workload. Enabling it creates a secondary index and impacts write throughput.)
- `TRUNCATE_SET`: Determines whether the data in the cluster should be truncated before the test starts. (Truncation on a large dataset may take time.)
- `RECORD_SIZE`: Average size (in bytes) for each record.
- `BATCH_READ_SIZE`: Number of requests in the batch read workload.
- `BATCH_WRITE_SIZE`: Number of requests in the batch write workload.
- `READ_HIT_RATIO`: A value between 0.0 and 1.0, indicating the percentage of read requests expected to find a record. (Read misses may exceed this value if a delete workload is active.)
- `KEY_CACHE_CAPACITY`: Number of record IDs that Perseus caches for future read, write, or delete queries. (Each key requires 8 bytes. For example, 1 billion entries require 8 GB of RAM.)
- `KEY_CACHE_SAVE_RATIO`: A value between 0.0 and 1.0, defining the percentage of inserted keys retained in the cache for reuse in read, update, and delete operations.

### setup.sh
Running this single file performs the following tasks, based on the configuration specified in configure.sh:
- Allocates instances in the specified AWS region.
- Prepares the instances and deploys an Aerospike cluster on them.
- Creates nodes designated for running Perseus.
- Deploys Perseus on those nodes and configures it to connect to the Aerospike cluster created above.
- Runs Perseus with a minimal workload.
- Open new terminals that tail the result of the perseus instances. (You must have iTerm installed.)
- Creates a node to host Grafana and Prometheus.
- Deploys Grafana and configures it to display Aerospike cluster metrics.
- Opens a browser window pointing to the Grafana dashboard. (Note: The username and password are both ‘admin’.)

This streamlined process sets up the entire environment for testing and monitoring with minimal effort.

### copyThreads.sh
Running this file copies the contents of threads.yaml to all nodes running Perseus. Details about threads.yaml can be found in the Perseus section.
To modify the workload dynamically, simply update threads.yaml and rerun copyThreads.sh.

### destroy.sh
Tears down the entire setup, including the Aerospike cluster, Perseus instances, and monitoring instances.

**NOTE**: Remember to run ```./destroy.sh```  when you don’t need the cluster. Running a large scale test is not cheap. If you don’t need the result, don’t waste money.

### cluster_setup.sh
Sets up an Aerospike cluster based on the specifications in configure.sh.

### cluster_destroy.sh
Destroys the Aerospike cluster without affecting other components.

### cluster_add_node.sh
Adds a new node to the Aerospike cluster. Once added, Aerospike will automatically start rebalancing the cluster.

### cluster_restart_node.sh
Restarts a previously stopped Aerospike node to simulate recovery. Provide the same index number used when stopping the node.

### cluster_stop_node.sh
Stops the Aerospike process on a specified node, reducing the number of active nodes in the cluster. You need to specify the index number of the node to stop.

### client_setup.sh
Sets up Perseus instances based on the specifications in configure.sh.

### client_destroy.sh
Destroys the Perseus nodes without affecting other components.

### client_connect.sh
Allows you to connect to a specific Perseus node by providing an index (minimum 1). Useful for diagnostics.

### client_rebuild.sh
Rebuilds the Perseus code across all nodes after changes are made. Before running this file, ensure your changes are pushed to the repository, as Perseus clones the repository before building. After running this file, the updated code is pulled, rebuilt, and restarted on all Perseus machines.

### client_build_perseus.sh
Similar to client_rebuild.sh, but does not automatically run Perseus after rebuilding.

### client_rerun.sh
Stops the Perseus process and reruns it.

### grafana_setup.sh
Configures the Grafana instance. Use this file if the Grafana setup in setup.sh fails for any reason.

### grafana_destroy.sh
Tears down the Grafana instance.
