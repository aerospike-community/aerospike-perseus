if [ -f "configure.sh" ]; then
    prefix=""
fi

if [ -f "../configure.sh" ]; then
    prefix="../"
fi

. $prefix"configure.sh"

echo "Creating the nodes"
aerolab client create base -c ${CLIENT_NUMBER_OF_NODES} -n ${CLIENT_NAME}  --instance-type ${CLIENT_INSTANCE_TYPE} --ebs=40 || exit 1

echo "Uploading the Perseus"
aerolab files upload -c -n ${CLIENT_NAME} $prefix"client/templates/perseus_setup.sh" /root/perseus_setup.sh || exit 1
aerolab files upload -c -n ${CLIENT_NAME} $prefix"client/templates/perseus.sh" /root/perseus.sh || exit 1

echo "Uploading the configuration.yaml"
nip=$(aerolab cluster list -j |grep -A7 ${CLUSTER_NAME} |grep IpAddress |head -1 |egrep -o '([0-9]{1,3}\.){3}[0-9]{1,3}')
Perseus_Conf=$prefix"client/templates/perseus_configuration_template.yaml"
sed "s/_NAMESPACE_/${NAMESPACE}/g" ${Perseus_Conf} | sed "s/_IP_/${nip}/g" > configuration.yaml
aerolab files upload -c -n ${CLIENT_NAME} configuration.yaml /root/configuration.yaml || exit 1
rm -rf configuration.yaml

echo "Building Perseus.sh"
aerolab client attach -n ${CLIENT_NAME} -l all --parallel -- bash /root/perseus_setup.sh
echo "Building Perseus.sh"
aerolab client attach -n ${CLIENT_NAME} -l all --detach --parallel -- bash /root/perseus.sh

