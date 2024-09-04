echo "Creating the nodes"
aerolab client create base -c ${CLIENT_NUMBER_OF_NODES} -n ${CLIENT_NAME}  --instance-type ${CLIENT_INSTANCE_TYPE} --ebs=40  --aws-expire=12h || exit 1
