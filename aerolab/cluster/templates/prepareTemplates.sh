STORAGE_ENGINE=""

if [ "${NAMESPACE_STORAGE_TYPE}" = "HMA" ]; then
  STORAGE_ENGINE="storage-engine device {\\n"
  #STORAGE_ENGINE+="\\t\\twrite-block-size 2048K\\n"
  OVERPROVISIONING=$(expr 100 - $CLUSTER_OVERPROVISIONING_PERCENTAGE)
  PARTITION_SIZE=$(expr $OVERPROVISIONING / $CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME)

  NVME_SETUP=""
  for (( i=1; i  <= CLUSTER_INSTANCE_NUMBER_OF_NVMES; i++ ))
  do
      NVME_ZERO=""
      NVME_SETUP+="blkdiscard /dev/nvme${i}n1\n"
      NVME_SETUP+="parted -s  /dev/nvme${i}n1 \"mktable gpt\"\n"
      for (( j=1; j  <= CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME; j++ ))
      do
        STORAGE_ENGINE+="\\t\\tdevice \/dev\/nvme${i}n1p${j}\\n"

        START=$(($PARTITION_SIZE*($j-1)))
        END=$(($PARTITION_SIZE*$j))
        NVME_SETUP+="parted -a optimal -s /dev/nvme${i}n1 \"mkpart primary ${START}% ${END}%\"\n"
        NVME_ZERO+="blkdiscard -z --length 8MiB /dev/nvme${i}n1p${j}\n"
      done
      NVME_SETUP+="sleep 10\n"
      NVME_SETUP+=$NVME_ZERO
      NVME_SETUP+="\n"
  done
  STORAGE_ENGINE+="\\t}"
  echo  $NVME_SETUP > nvme_setup.sh
fi

if [ "${NAMESPACE_STORAGE_TYPE}" = "MEMORY" ]; then
  STORAGE_ENGINE="storage-engine memory\\n"
  STORAGE_ENGINE+="\\tpartition-tree-sprigs 4096"
fi

if [[ $VER == 6* ]]; then
  _MEMORY_SIZE_="memory-size 1T"
else
  _MEMORY_SIZE_=""
fi

# prepare Aerospike.conf file
Aerospike_Conf=$PREFIX"/../cluster/templates/aerospike_template.conf"
sed "s/_NAMESPACE_NAME_/${NAMESPACE_NAME}/g" ${Aerospike_Conf} |
sed "s/_NAMESPACE_REPLICATION_FACTOR_/${NAMESPACE_REPLICATION_FACTOR}/g" |
sed "s/_MEMORY_SIZE_/${_MEMORY_SIZE_}/g" |
sed "s/_DEFAULT_TTL_/${NAMESPACE_DEFAULT_TTL}/g" |
sed "s/_STORAGE_ENGINE_/${STORAGE_ENGINE}/g" > aerospike.conf