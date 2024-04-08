if [ -f "configure.sh" ]; then
    prefix=""
fi

if [ -f "../configure.sh" ]; then
    prefix="../"
fi

. $prefix"configure.sh"
. $prefix"cluster/templates/prepareTemplates.sh"

# create cluster
New_Node_Number=$(aerolab cluster list | wc -l)
New_Node_Number=$((New_Node_Number-2))
New_Node_Number=11

echo "Grow the cluster"
aerolab cluster grow -n ${CLUSTER_NAME} -v ${VER} -o aerospike.conf --instance-type ${CLUSTER_INSTANCE_TYPE} --ebs=20 --start=n || exit 1
rm -rf aerospike.conf

if [ "${CLUSTER_STORAGE_TYPE}" = "HYBRID" ]; then
  echo "Configure NVMe"
  aerolab files upload -n ${CLUSTER_NAME} -l ${New_Node_Number} nvme_setup.sh /root/nvme_setup.sh || exit 1
  rm -f nvme_setup.sh
  aerolab attach shell -n ${CLUSTER_NAME} -l ${New_Node_Number} -- bash /root/nvme_setup.sh || exit 1
  aerolab aerospike start -n ${CLUSTER_NAME} -l ${New_Node_Number}
fi

# let the cluster do it's thing
echo "Wait"
sleep 10

# exporter
echo "Adding exporter"
aerolab cluster add exporter -n ${CLUSTER_NAME} -l ${New_Node_Number} -o $prefix"cluster/templates/ape.toml"

echo "Reconfiguring Prometheus"
aerolab client configure ams -n ${GRAFANA_NAME} -s ${CLUSTER_NAME}