# AWS Detail
AWS_REGION="eu-west-1"

# Cluster Detail
VER="7.1.0.5"
CLUSTER_NAME="Tremend"
NAMESPACE="Test"
CLUSTER_STORAGE_TYPE="HYBRID" #HYBRID OR MEMORY
CLUSTER_REPLICATION_FACTOR=2
CLUSTER_NUMBER_OF_NODES="3"
CLUSTER_INSTANCE_TYPE="i4i.4xlarge"
CLUSTER_INSTANCE_NUMBER_OF_NVMES="1"
CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME="4"

# GRAFANA Detail
GRAFANA_NAME=${CLUSTER_NAME}"_GRAFANA"
GRAFANA_INSTANCE_TYPE="t3.xlarge"

# Client Detail
CLIENT_NAME="Perseus_${CLUSTER_NAME}"
CLIENT_INSTANCE_TYPE="c6a.32xlarge"
CLIENT_NUMBER_OF_NODES=1

# Perseus Additional Testcases
KEY_CACHE_CAPACITY=500000000 #The instance must have enough RAM to keep the key cache in memory. Each entry is 8 Bytes. 1 billion entries need 8 GB of Ram
KEY_CACHE_SAVE_RATIO=0.5
TRUNCATE_SET=True
STRING_INDEX=True
NUMERIC_INDEX=True
GEO_SPATIAL_INDEX=False
UDF_AFFREGATION=False

# Perseus Details
RECORD_SIZE=1000 #Bytes
BATCH_READ_SIZE=50
BATCH_WRITE_SIZE=50
READ_HIT_RATIO=0.8

# setup backend
aerolab config backend -t aws -r ${AWS_REGION}
