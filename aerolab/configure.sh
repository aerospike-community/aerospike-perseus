# AWS Detail
AWS_REGION="eu-west-1"

# Cluster Detail
VER="7.0.0.7"
CLUSTER_NAME="Amadeus-1"
NAMESPACE="load_test"
CLUSTER_STORAGE_TYPE="HYBRID" #HYBRID OR MEMORY
CLUSTER_REPLICATION_FACTOR=2
CLUSTER_NUMBER_OF_NODES="3"
CLUSTER_INSTANCE_TYPE="i3en.3xlarge"
CLUSTER_INSTANCE_NUMBER_OF_NVMES="1"
CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME="6"
FEATURES="/Users/behradbabaee/Aerospike/features.conf"

# GRAFANA Detail
GRAFANA_NAME=${CLUSTER_NAME}"_GRAFANA"
GRAFANA_INSTANCE_TYPE="t3.xlarge"

# Client Detail
CLIENT_NAME="Perseus_${CLUSTER_NAME}"
CLIENT_INSTANCE_TYPE="c7g.16xlarge"
CLIENT_NUMBER_OF_NODES=1

# setup backend
[ "${AWS_REGION}" = "" ] && aerolab config backend -t docker || aerolab config backend -t aws -r ${AWS_REGION}
aerolab config defaults -k '*FeaturesFilePath' -v ${FEATURES} || exit 1
