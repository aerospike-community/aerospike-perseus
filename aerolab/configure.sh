# AWS Detail
AWS_REGION="eu-west-1"

# Cluster Detail
VER="6.2.0.7"
CLUSTER_NAME="Demo"
NAMESPACE="demo"
CLUSTER_STORAGE_TYPE="HYBRID" #HYBRID OR MEMORY
CLUSTER_REPLICATION_FACTOR=2
CLUSTER_NUMBER_OF_NODES="4"
CLUSTER_INSTANCE_TYPE="i4i.4xlarge"
CLUSTER_INSTANCE_USABLE_MEMORY="120G"
CLUSTER_INSTANCE_NUMBER_OF_NVMES="1"
CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME="8"
FEATURES="/Users/behradbabaee/Aerospike/features.conf"

# GRAFANA Detail
GRAFANA_NAME=${CLUSTER_NAME}"_GRAFANA"
GRAFANA_INSTANCE_TYPE="t3.xlarge"

# Client Detail
CLIENT_NAME="Perseus"
CLIENT_INSTANCE_TYPE="c7g.4xlarge"
CLIENT_NUMBER_OF_NODES=5

# setup backend
[ "${AWS_REGION}" = "" ] && aerolab config backend -t docker || aerolab config backend -t aws -r ${AWS_REGION}
aerolab config defaults -k '*FeaturesFilePath' -v ${FEATURES} || exit 1
