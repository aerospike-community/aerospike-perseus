. configure.sh

# prepare config files
TNF=templates/aerospike_template.conf
sed "s/_NAMESPACE_/${NAMESPACE}/g" ${TNF} > aerospike.conf

# create cluster
echo "Creating cluster"
aerolab cluster create -n ${CLUSTER_NAME} -c ${NUMBER_OF_NODES} -v ${VER} -o aerospike.conf --instance-type ${AWS_SERVER_INSTANCE} --ebs=20 --secgroup-id=${SEC_GROUP} --subnet-id=${SUBNET} || exit 1

rm -f aerospike.conf


echo "Configure cluster"
aerolab files upload -n ${CLUSTER_NAME} templates/nvme_setup.sh /root/nvme_setup.sh || exit 1
aerolab attach shell -n ${CLUSTER_NAME} -l all -- bash /root/nvme_setup.sh || exit 1
aerolab aerospike stop -n ${CLUSTER_NAME} -l all
sleep 5
aerolab aerospike start -n ${CLUSTER_NAME} -l all

# let the cluster do it's thing
echo "Wait"
sleep 15

# exporter
echo "Adding exporter"
aerolab cluster add exporter -n ${CLUSTER_NAME} -o templates/ape.toml || exit 1