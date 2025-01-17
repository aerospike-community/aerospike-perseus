# prepare Aerospike.conf file
Aerospike_Conf=$PREFIX"/../cluster/templates/aerospike_template.conf"
sed "s/_NAMESPACE_NAME_/${NAMESPACE_NAME}/g" ${Aerospike_Conf} |
sed "s/_NAMESPACE_REPLICATION_FACTOR_/${NAMESPACE_REPLICATION_FACTOR}/g" |
sed "s/_DEFAULT_TTL_/${NAMESPACE_DEFAULT_TTL}/g" > aerospike.conf