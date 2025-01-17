# AWS Detail
AWS_REGION="eu-west-1"
AWS_EXPIRE="0" # length of life of nodes prior to expiry; seconds, minutes, hours, ex 20h 30m. 0 for no expiry.

# Aerospike Detail
VER="7.2.0.4"
NAMESPACE_NAME="Test"
NAMESPACE_DEFAULT_TTL="0"
NAMESPACE_STORAGE_TYPE="HMA" #HMA OR MEMORY
NAMESPACE_REPLICATION_FACTOR=2

# Cluster Detail
CLUSTER_NAME="Miro"
CLUSTER_NUMBER_OF_NODES="4"
CLUSTER_INSTANCE_TYPE="r6gd.4xlarge"
CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME="5"
CLUSTER_OVERPROVISIONING_PERCENTAGE=10

# GRAFANA Detail
GRAFANA_NAME=${CLUSTER_NAME}"_GRAFANA"
GRAFANA_INSTANCE_TYPE="t3.xlarge"

# Client Detail
CLIENT_NAME="Perseus_${CLUSTER_NAME}"
CLIENT_INSTANCE_TYPE="c6i.4xlarge" #Choose instances with more cpus, more than 32 GB of RAM, and no NVMe. C6a family are good choices.
CLIENT_NUMBER_OF_NODES=1

# Testcases
STRING_INDEX=False
NUMERIC_INDEX=False
GEO_SPATIAL_INDEX=True
UDF_AFFREGATION=False
TRUNCATE_SET=False

# Workload Details
RECORD_SIZE=500 #Bytes. Should be larger than 350!
BATCH_READ_SIZE=100
BATCH_WRITE_SIZE=50
READ_HIT_RATIO=0.80

# Perseus Detail
KEY_CACHE_CAPACITY=500000000 #The instance must have enough RAM to keep the key cache in memory. Each entry is 8 Bytes. 1 billion entries need 8 GB of Ram
KEY_CACHE_SAVE_RATIO=1.0

# setup backend
aerolab config backend -t aws -r ${AWS_REGION}
