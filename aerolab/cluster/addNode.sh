. $PREFIX"/../cluster/templates/prepareTemplates.sh"

# create cluster

echo "Grow the cluster"
aerolab cluster grow -n ${CLUSTER_NAME} -v ${VER} -o aerospike.conf --instance-type ${CLUSTER_INSTANCE_TYPE} --ebs=20 --start=n || exit 1
rm -rf aerospike.conf

New_Node_Number=$(aerolab cluster list -i | grep "cluster="${CLUSTER_NAME}" "| wc -l  | xargs)

if [ "${NAMESPACE_STORAGE_TYPE}" = "MEMORY" ]; then
  echo "Configure In-Memory"
  aerolab conf namespace-memory --name=${CLUSTER_NAME} --namespace=${NAMESPACE_NAME} --nodes=${New_Node_Number} --mem-pct=75
fi
if [ "${NAMESPACE_STORAGE_TYPE}" = "HMA" ]; then
  echo "Configure NVMe"
  OVERPROVISIONING=$(expr 100 - $CLUSTER_OVERPROVISIONING_PERCENTAGE)
  PARTITION_SIZE=$(expr $OVERPROVISIONING / $CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME)
  for i in $(seq 1 $CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME); do P+=${PARTITION_SIZE}","; done
  aerolab cluster partition create --name=${CLUSTER_NAME} --nodes=${New_Node_Number} --filter-type=nvme --partitions=${P%?} || exit 1
  aerolab cluster partition conf --name=${CLUSTER_NAME} --nodes=${New_Node_Number} --namespace=${NAMESPACE_NAME} --filter-type=nvme --configure=device || exit 1
fi

aerolab aerospike start -n ${CLUSTER_NAME} -l ${New_Node_Number}

# let the node join the cluster
echo "Wait"
sleep 10

# exporter
echo "Adding exporter"
aerolab cluster add exporter -n ${CLUSTER_NAME} -l ${New_Node_Number} -o $PREFIX"/../cluster/templates/ape.toml"

echo "Reconfiguring Prometheus"
aerolab client configure ams -n ${GRAFANA_NAME} -s ${CLUSTER_NAME}