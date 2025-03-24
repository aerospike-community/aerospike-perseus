# AWS Detail
AWS_REGION="us-east-2"
AWS_EXPIRE="6h" # length of life of nodes prior to expiry; seconds, minutes, hours, ex 20h 30m. 0 for no expiry.

# Aerospike Detail
VER="8.0.0.4"
NAMESPACE_NAME="Test"
NAMESPACE_DEFAULT_TTL="0"
NAMESPACE_STORAGE_TYPE="HMA" #HMA, MEMORY, ALL_FLASH
NAMESPACE_REPLICATION_FACTOR=2
SECONDARY_INDEX_STORAGE="MEMORY" #MEMORY, FLASH

# Cluster Detail
CLUSTER_NAME="MetaMapping"
CLUSTER_NUMBER_OF_NODES="2"
CLUSTER_INSTANCE_TYPE="i4g.4xlarge"
CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME="10"
CLUSTER_OVERPROVISIONING_PERCENTAGE=10

#Primary Or Secondary Index on Flash
PRIMARY_INDEX_STORAGE_PARTITIONS="1-2"
PARTITION_TREE_SPRIGS=262144
DATA_STORAGE_PARTITIONS="5-10" # Set if either the primary or secondary indexes are stored on flash.
SECONDARY_INDEX_STORAGE_PARTITIONS="3-4" # Set if the secondary indices are on flash.

# GRAFANA Detail
GRAFANA_NAME=${CLUSTER_NAME}"_GRAFANA"
GRAFANA_INSTANCE_TYPE="t3.xlarge"

# Client Detail
CLIENT_NAME="Perseus_${CLUSTER_NAME}"
CLIENT_INSTANCE_TYPE="c6i.8xlarge" #Choose instances with more cpus, more than 32 GB of RAM, and no NVMe. C6a family are good choices.
CLIENT_NUMBER_OF_NODES=1
TRUNCATE_SET=False

# Testcases
STRING_INDEX=True
NUMERIC_INDEX=True
GEO_SPATIAL_INDEX=False
UDF_AFFREGATION=False
RANGE_QUERY=True

# Range Query and UDF Aggregation range generator config
NORMAL_RANGE=5
MAX_RANGE=100
CHANCE_OF_MAX=.00001

# Workload Details
RECORD_SIZE=500 #Bytes. This test doesn't allow records smaller than 178 bytes!
BATCH_READ_SIZE=100
BATCH_WRITE_SIZE=50
READ_HIT_RATIO=1.0

# Perseus Detail
KEY_CACHE_CAPACITY=500000000 #The instance must have enough RAM to keep the key cache in memory. Each entry is 8 Bytes. 1 billion entries need 8 GB of Ram
KEY_CACHE_SAVE_RATIO=1.0

# setup backend
aerolab config backend -t aws -r ${AWS_REGION}
