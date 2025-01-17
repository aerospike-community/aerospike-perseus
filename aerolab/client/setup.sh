echo "Creating the nodes"
aerolab client create base -c ${CLIENT_NUMBER_OF_NODES} -n ${CLIENT_NAME}  --instance-type ${CLIENT_INSTANCE_TYPE} --ebs=50  --aws-expire=${AWS_EXPIRE} || exit 1
