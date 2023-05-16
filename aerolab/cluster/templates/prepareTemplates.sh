if [ -f "configure.sh" ]; then
    prefix=""
fi

if [ -f "../configure.sh" ]; then
    prefix="../"
fi

if [ -f "../../configure.sh" ]; then
    prefix="../../"
fi

. $prefix"configure.sh"

STORAGE_ENGINE=""

if [ "${CLUSTER_STORAGE_TYPE}" = "HYBRID" ]; then
  STORAGE_ENGINE="storage-engine device {\\n"
  STORAGE_ENGINE+="\\t\\twrite-block-size 1024K\\n"
  PARTITION_SIZE=$(expr 80 / $CLUSTER_INSTANCE_NUMBER_OF_PARTITION_ON_EACH_NVME)
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
  STORAGE_ENGINE+="\\t\\tdata-in-memory false\\n"
  STORAGE_ENGINE+="\\t}"
  echo  $NVME_SETUP > nvme_setup.sh
fi

if [ "${CLUSTER_STORAGE_TYPE}" = "MEMORY" ]; then
  STORAGE_ENGINE="storage-engine memory"
fi

# prepare Aerospike.conf file
Aerospike_Conf=$prefix"cluster/templates/aerospike_template.conf"
sed "s/_NAMESPACE_/${NAMESPACE}/g" ${Aerospike_Conf} |
sed "s/_MEMORY_SIZE_/${CLUSTER_INSTANCE_USABLE_MEMORY}/g" |
sed "s/_REPLICATION_FACTOR_/${CLUSTER_REPLICATION_FACTOR}/g" |
sed "s/_STORAGE_ENGINE_/${STORAGE_ENGINE}/g" > aerospike.conf