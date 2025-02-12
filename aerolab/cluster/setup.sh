. $PREFIX"/../cluster/templates/prepareTemplates.sh"

# create cluster
echo "Creating cluster"
aerolab cluster create -n ${CLUSTER_NAME} -c ${CLUSTER_NUMBER_OF_NODES} -v ${VER} -o aerospike.conf --instance-type ${CLUSTER_INSTANCE_TYPE} --ebs=25 --start=n --aws-expire=${AWS_EXPIRE} || exit 1
rm -rf aerospike.conf

if [ "${NAMESPACE_STORAGE_TYPE}" = "MEMORY" ]; then
  echo "Configure In-Memory"
  aerolab conf namespace-memory -n ${CLUSTER_NAME} --namespace=${NAMESPACE_NAME} --mem-pct=75
fi
if [ "${NAMESPACE_STORAGE_TYPE}" = "HMA" ]; then
  echo "Configure NVMe"
  OVERPROVISIONING=$(expr 100 - $CLUSTER_OVERPROVISIONING_PERCENTAGE)
  PARTITION_SIZE=$(expr $OVERPROVISIONING / $CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME)
  for i in $(seq 1 $CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME); do P+=${PARTITION_SIZE}","; done
  aerolab cluster partition create -n ${CLUSTER_NAME} -t nvme -p ${P%?} || exit 1
  aerolab cluster partition conf -n ${CLUSTER_NAME} --namespace=${NAMESPACE_NAME} --filter-type=nvme --configure=device || exit 1
fi

aerolab aerospike start -n ${CLUSTER_NAME} -l all

echo "Wait..."
sleep 10

# exporter
echo "Adding exporter"
aerolab cluster add exporter -n ${CLUSTER_NAME} -o $PREFIX"/../cluster/templates/ape.toml"
