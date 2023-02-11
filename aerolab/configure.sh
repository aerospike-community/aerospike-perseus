# Cluster Detail
VER="6.2.0.2"
CLUSTER_NAME="Demo"
NAMESPACE="demo"
NUMBER_OF_NODES="4"
FEATURES="/Users/behradbabaee/Aerospike/features.conf"

# Client Detail
CLIENT_NAME="Perseus"

# AWS Detail
AWS_REGION="eu-west-1"
AWS_SERVER_INSTANCE="i4i.4xlarge"
AWS_CLIENT_INSTANCE="c5.9xlarge"
SEC_GROUP="sg-09d325619660fbcda"
SUBNET="subnet-068095f2fe8123a83"

# aerolab config file
export AEROLAB_CONFIG_FILE="aerolab.conf"
rm -f ${AEROLAB_CONFIG_FILE}

# setup backend
[ "${AWS_REGION}" = "" ] && aerolab config backend -t docker || aerolab config backend -t aws -r ${AWS_REGION}
aerolab config defaults -k '*FeaturesFilePath' -v ${FEATURES} || exit 1
