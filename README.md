# 🛡️ Perseus
Perseus is a powerful benchmarking tool designed to stress-test and validate Aerospike clusters under real-world conditions. It simulates diverse workloads—reads, writes, deletes, updates, and secondary index queries—with fine-grained control and visibility. Whether you're tuning for ultra-low latency or validating infrastructure scalability, Perseus gives you the precision and performance insights you need.

Perseus works hand-in-hand with Pegasus, a companion orchestration tool that automates the deployment of Aerospike clusters, clients, and monitoring dashboards to run large-scale tests seamlessly. The tools are named after their mythological counterparts: Perseus, the hero who defeated Medusa, and Pegasus, the winged horse born from her blood. Just as Perseus and Pegasus joined forces to complete legendary quests, Aerospike-Perseus and Aerospike-Pegasus combine to deliver robust, automated, end-to-end performance testing.

# 🚀 Introduction
**Perseus** is a benchmarking tool designed to test and showcase the core capabilities of **Aerospike** technology. It can generate various workloads including reads, writes, updates, deletes, expressions, batch operations, User Defined Functions (via LUA), secondary index queries, and aggregations.

Compared to tools like `asbench` and `YCSB`, **Perseus** offers several advantages:

- Supports testing of advanced features beyond basic read/write operations.
- Dynamic workload control by modifying the number of threads during runtime.
- Open source and easy to customise.
- Capable of generating heavy and long-lasting workloads (provided sufficient resources).

# 🔧 Prerequisites

- **JDK 22+**: Perseus requires Java Development Kit version 22 or later.  
  👉 [Install JDK](https://openjdk.org/install/)  
  If you're using [Homebrew](https://brew.sh/), simply run:
  ```bash
  brew install openjdk
  ```

- **Maven**: Required for building the project.  
  👉 [Install Maven](https://maven.apache.org/install.html)  
  Using Homebrew:
  ```bash
  brew install maven
  ```

- **Aerospike Cluster**: You need access to an Aerospike cluster. If you don’t have one, the next section includes instructions for running a local cluster.

# 🛠️ Building Perseus

To build the executable run the following command when you are in the directory that contains the `pom.xml` file:

```bash
mvn package
```

If successful, the JAR will be available at:

```
./target/perseus-1.0-SNAPSHOT.jar
```

# 🧪 Running a Local Aerospike Cluster

If you don't have access to an existing Aerospike cluster—or prefer not to set one up manually—you can easily spin up a local instance using Docker.

This is the quickest way to test **Perseus**. However, please note:

> ⚠️ **Important Considerations**  
> - A single-node cluster has **limited resources**, making it unsuitable for performance benchmarking.  
> - With high write rates (e.g. tens of thousands of requests per second), the container may run out of memory or storage within minutes.  
> - This setup is best used for **functional testing** rather than stress testing.

## ✅ Prerequisites

Before proceeding, ensure the following:

- **Docker Desktop** is installed:  
  👉 [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)

- The latest Aerospike server images are available at:  
  👉 [Docker Hub – Aerospike](https://hub.docker.com/_/aerospike)

- **Perseus compatibility**:
  - You can test **all features** except for the **delete workload** using the **Aerospike Community Edition** (`ce`).
  - To test **delete workloads**, you'll need the **Enterprise Edition** (`ee`), which requires a feature file.
    - Get a **temporary trial license** here:  
      👉 [Aerospike Enterprise Trial](https://aerospike.com/get-started-aerospike-database/)

## 🚀 Launching a One-Node Aerospike Cluster

To start a local in-memory Aerospike cluster using Docker, run:

```bash
docker run --rm -d --name aerospike -p 3000-3002:3000-3002 aerospike:ce-8.0.0.4
```

This command:
- Runs Aerospike Community Edition in the background
- Exposes ports `3000–3002` on `localhost`
- Starts with default, in-memory configuration

> ✅ **Perseus defaults to `localhost:3000`**, so it will work out of the box with this setup.

## 🔍 Useful Docker Commands

To **attach to the running container** (e.g. to inspect logs or modify configs):

```bash
docker exec -it aerospike bash
```

To **view Aerospike logs**:

```bash
docker logs aerospike
```

To **stop the cluster** (e.g. if it runs out of space or memory):

```bash
docker container stop aerospike
```

> 💡 **Tip**: If you run into resource limits, the simplest fix is to stop and restart the container. Since it runs in-memory by default, no data is persisted.

# ▶️ Running Perseus

Running Perseus is straightforward. It relies on four configuration files:

```
├── configuration.yaml
├── threads.yaml
├── same_as_expression.lua
└── udf_aggregation.lua
```

Perseus looks for these files in the current working directory. If they are not present, it will copy default versions there and proceed with those.

The default configuration assumes your Aerospike cluster is running on `localhost`. If not, the run will fail.

### ⏱ Quick Tip:
Run the executable once (even if it fails) to generate the config files in your working directory. Then modify them as needed for the subsequent runs.

### Run Command:

First ensure you're using JDK 22 or newer:
```bash
java -version
```
Then:
```bash
java -jar perseus-1.0-SNAPSHOT-jar-with-dependencies.jar
```

# ⚙️ Configuring Perseus

Perseus uses four configuration files, but only two typically require modification:
- `configuration.yaml`
- `threads.yaml`

The other two (`same_as_expression.lua` and `udf_aggregation.lua`) are used for UDF-based workloads and rarely need to be changed.

## 📄 `configuration.yaml`

This file is loaded once at the beginning of execution.

### 🔌 `aerospikeConfiguration`

Connection details for your Aerospike cluster:
- **`hosts`**: List of cluster nodes. You only need to specify one node — the client will auto-discover the rest. However the format for adding more nodes is like this: 
  ```yaml
  hosts: 
    -
      ip: IP1
      port: PORT1
    -
      ip: IP2
      port: PORT2
  ```
- **`username` / `password`**: Required only if authentication is enabled.
- **`namespace`**: Target Aerospike namespace.
- **`set`**: Set name used for the test.
- **`truncateSet`**: If `true`, Perseus will truncate the set at startup.

  > ⚠️ **Note:**  
  > - Truncation only affects pre-existing data. On a fresh cluster, it has no effect.  
  > - Truncating large datasets can take time and may delay test start.

### 📤 `outputWindowConfiguration`

Controls how frequently Perseus prints test metrics to the console:
- **`printIntervalSec`**: Interval between each output line (in seconds).
- **`numberOfLinesBeforeReprintingTheHeader`**: Ensures headers are reprinted periodically to preserve readability.

### 🧠 `testConfiguration`

This section governs data generation, caching, and workload behaviour.

#### 🔁 Key Caching

Perseus generates random records during insertion. To ensure later read/update/delete workloads can operate on real data, keys are cached in memory:

- **`cacheCapacity`**: Maximum number of keys cached in memory. Each key consumes 8 bytes.

  > Example:  
  1 billion keys ≈ 8 GB RAM

  The cache uses a circular buffer — when full, it overwrites the oldest entries.

  > 💡 **Tip:**  
  The full key cache buffer is allocated at startup. Setting a very high cacheCapacity can consume significant memory on the machine running Perseus. Be mindful of available RAM to avoid system instability.

- **`discardRatio`**: A value between `0.0` and `1.0` controlling what percentage of keys are retained.
  - `1.0` means the cache keeps only the most recent `cacheCapacity` entries.
  - Lower values distribute cached keys more evenly across the entire insert history.

  > 💡 **Tip:**  
  Unless you're inserting billions of records, you can safely use `1.0` to cache all inserted keys.

#### 📦 Record and Batch Settings

- **`recordSize`**: Average size (in bytes) of records inserted. Sizes follow a normal distribution centred on this value. The size cannot be smaller than 173 bytes. 

- **`readBatchSize`**: Number of records per batch in batch read workloads.

  > ⚠️ Must be a positive, non-zero integer.

- **`writeBatchSize`** : Number of records per batch in batch write workloads.

  > ⚠️ Must also be a positive, non-zero integer.

- **`readHitRatio`**: Proportion of reads expected to return valid results (i.e. the record exists).  
  Value must be between `0.0` and `1.0`.

  > ⚠️ If delete workloads are enabled, actual hit ratios may be lower than configured due to record removal.

### 🔍 Indexing & Query Workloads

Perseus can enable various query workloads, each backed by secondary indexes. If you don’t need them, **disable these options** to reduce memory usage and write latency.

- **`stringIndex`**: Creates a **STRING** secondary index and enables string-based query workloads.  
  Each query targets a known inserted string value.

- **`numericIndex`** : Creates a **NUMERIC** index and enables numeric queries.  
  Targets known values, typically returning a single record unless it has been deleted.

- **`geoSpatialIndex`**: Creates a **GeoSpatial** index and enables geo query workloads.  
  Queries are randomly generated and may return 0, 1, or multiple records.

- **`udfAggregation`**: Creates a numeric index and enables **UDF-based aggregation**.  
  Queries operate on numeric ranges, and behaviour is further defined below.

- **`rangeQuery`**: Enables range-based secondary index queries on numeric values.  
  Like UDF aggregation, queries a range of records based on insert order.

### 🔢 Range Query Parameters

These apply only if `rangeQuery` or `udfAggregation` is enabled:

- **`NORMAL_RANGE`**: Determines the typical range size queried.  
  Perseus inserts an ever-increasing number into each record. Queries against `[X, X + NORMAL_RANGE]` return 1 or more records.

- **`MAX_RANGE`**: Size of the largest possible query range, used occasionally to simulate edge cases.

- **`CHANCE_OF_MAX`**: Probability (percentage) that a query will use `MAX_RANGE` instead of `NORMAL_RANGE`.


## ⚙️ `threads.yaml`

The `threads.yaml` file controls the **number of threads assigned to each workload** and can be modified **in real time** while Perseus is running. This dynamic configuration allows you to **reshape the active workload on the fly**, enabling powerful and flexible testing scenarios.

Each entry in the file specifies the number of threads assigned to a particular workload. Below is a detailed explanation of the available workloads and how they interact with the system:

### 🧱 Core Workloads

> 🔑 **Note:** The following workloads **insert new records**, whose keys are **cached** in memory (keyCache). This enables other workloads (e.g. read/update/delete) to operate on known existing records. You can configure the cache in `configuration.yaml`.

- **`write`**: Inserts random records with an average size defined by `recordSize` in `configuration.yaml`.

- **`batchWrite`**: Performs batch inserts. The batch size is defined by `writeBatchSize` in `configuration.yaml`.

### 📖 Accessing Existing Records

> 🔁 **Note:** These workloads operate on records stored in the **keyCache**, ensuring that they access known existing data.

- **`read`**: Performs individual record reads. The ratio of successful hits is controlled by `readHitRatio` in `configuration.yaml`.

- **`batchRead`**: Similar to `read`, but processes records in batches. Batch size is defined by `readBatchSize` in `configuration.yaml`.

- **`update`**: Updates existing records by appending a new bin with a new value.

- **`delete`**: Permanently deletes existing records.

### 🧮 Expression and UDF Workloads

> 🧠 **Note:** 
>  - These workloads also operate on records stored in the **keyCache**, ensuring that they access known existing data.
>  - These workloads implement identical logic using different execution methods:  
>   They subtract the value of one bin from another and return `"Yes"` if the result equals a predefined value; otherwise, `"No"`.

- **`expressionRead`**: Executes the expression and returns the result as part of the response.

- **`expressionWrite`**: Evaluates the expression and stores the result in a new bin.

- **`udf`**: Similar to `expressionWrite`, but implemented as a **User Defined Function** (UDF) in **Lua**.  
  > 📄 The Lua source code uploaded by Perseus is located in `same_as_expression.lua`.

### 🔍 Secondary Index and Aggregation Workloads

> ⚠️ **Note:** 
>  - These workloads also operate on records stored in the **keyCache**, ensuring that they access known existing data.
> - These workloads are only available if explicitly **enabled** in `configuration.yaml`. They are **resource-intensive**, and running multiple of them simultaneously can significantly reduce throughput.

- **`numericSearch`**: Queries a **Numeric** secondary index to retrieve a single record.

- **`stringSearch`**: Queries a **String** secondary index to retrieve a single record.

- **`geospatialSearch`**: Uses a **GeoSpatial** secondary index to retrieve a matching record based on location.

- **`udfAggregation`**: Performs a **range-based aggregation** using a Lua UDF and a numeric secondary index.  
  Calculates the **average value** across multiple matching records.

  > 📄 The source code for this UDF is located in `udf_aggregation.lua`.

- **`rangeQuery`**: Performs a **range-based query** on a numeric secondary index and return multiple records. 

### 🧮 Range Query Configuration

The following parameters fine-tune the behaviour of the **`rangeQuery`** and **`udfAggregation`** workloads.

- **`normalTimeRange`**: The standard size of the range queried. For example, if the most recently inserted record has a numeric value of `X`, a query might retrieve records in the range `[X, X + normalTimeRange]`.

This setting models **common, narrowly scoped** queries that return a small number of records.

- **``maxTimeRange`**: The **maximum range** size, used occasionally to simulate broader, more expensive queries. These represent **edge cases** or analytical workloads that scan a larger swath of data.

When selected, the query will retrieve records in a much wider range (e.g. `[X, X + maxTimeRange]`).

- **``maxTimeRangeChance`**: A floating point number between `0.0` and `1.0` representing the **probability** that a given query will use `maxTimeRange` instead of `normalTimeRange`.

For example:
- `0.00001` means only **1 in 100,000** queries will use the larger range.
- This allows you to occasionally introduce **resource-heavy queries** without overwhelming the system.