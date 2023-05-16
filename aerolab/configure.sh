# AWS Detail
AWS_REGION="eu-west-1"

# Cluster Detail
VER="6.3.0.2"
CLUSTER_NAME="Booking"
NAMESPACE="demo"
CLUSTER_STORAGE_TYPE="HYBRID" #HYBRID OR MEMORY
CLUSTER_REPLICATION_FACTOR=2
CLUSTER_NUMBER_OF_NODES="6"
CLUSTER_INSTANCE_TYPE="i4i.32xlarge"
CLUSTER_INSTANCE_USABLE_MEMORY="1000G"
CLUSTER_INSTANCE_NUMBER_OF_NVMES="8"
CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME="8"
FEATURES="/Users/behradbabaee/Aerospike/features.conf"

# GRAFANA Detail
GRAFANA_NAME=${CLUSTER_NAME}"_GRAFANA"
GRAFANA_INSTANCE_TYPE="t3.xlarge"

# Client Detail
CLIENT_NAME="Perseus"
CLIENT_INSTANCE_TYPE="c7g.8xlarge"
CLIENT_NUMBER_OF_NODES=4

# setup backend
[ "${AWS_REGION}" = "" ] && aerolab config backend -t docker || aerolab config backend -t aws -r ${AWS_REGION}
aerolab config defaults -k '*FeaturesFilePath' -v ${FEATURES} || exit 1
