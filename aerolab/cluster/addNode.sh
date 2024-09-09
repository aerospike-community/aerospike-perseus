. $PREFIX"/../cluster/templates/prepareTemplates.sh"

# create cluster
New_Node_Number=$(aerolab cluster list -i| wc -l)

echo "Grow the cluster"
aerolab cluster grow -n ${CLUSTER_NAME} -v ${VER} -o aerospike.conf --instance-type ${CLUSTER_INSTANCE_TYPE} --ebs=20 --start=n || exit 1
rm -rf aerospike.conf

if [ "${NAMESPACE_STORAGE_TYPE}" = "HMA" ]; then
  echo "Configure NVMe"
  aerolab files upload -n ${CLUSTER_NAME} -l ${New_Node_Number} nvme_setup.sh /root/nvme_setup.sh || exit 1
  rm -f nvme_setup.sh
  aerolab attach shell -n ${CLUSTER_NAME} -l ${New_Node_Number} -- bash /root/nvme_setup.sh || exit 1
  aerolab aerospike start -n ${CLUSTER_NAME} -l ${New_Node_Number}
fi

# let the node join the cluster
echo "Wait"
sleep 10

# exporter
echo "Adding exporter"
aerolab cluster add exporter -n ${CLUSTER_NAME} -l ${New_Node_Number} -o $PREFIX"/../cluster/templates/ape.toml"

echo "Reconfiguring Prometheus"
aerolab client configure ams -n ${GRAFANA_NAME} -s ${CLUSTER_NAME}