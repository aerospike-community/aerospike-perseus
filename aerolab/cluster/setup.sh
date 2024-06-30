. $PREFIX"/../cluster/templates/prepareTemplates.sh"

# create cluster
echo "Creating cluster"
aerolab cluster create -n ${CLUSTER_NAME} -c ${CLUSTER_NUMBER_OF_NODES} -v ${VER} -o aerospike.conf --instance-type ${CLUSTER_INSTANCE_TYPE} --ebs=20 --start=n --aws-expire=0 || exit 1
rm -rf aerospike.conf

if [ "${CLUSTER_STORAGE_TYPE}" = "HYBRID" ]; then
  echo "Configure NVMe"
  aerolab files upload -n ${CLUSTER_NAME} nvme_setup.sh /root/nvme_setup.sh || exit 1
  rm -f nvme_setup.sh
  aerolab attach shell -n ${CLUSTER_NAME} -l all -- bash /root/nvme_setup.sh || exit 1
fi

aerolab aerospike start -n ${CLUSTER_NAME} -l all

# let the cluster do it's thing
echo "Wait"
sleep 10

# exporter
echo "Adding exporter"
aerolab cluster add exporter -n ${CLUSTER_NAME} -o $PREFIX"/../cluster/templates/ape.toml"
