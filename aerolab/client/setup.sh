cho "Creating the nodes"
aerolab client create base -c ${CLIENT_NUMBER_OF_NODES} -n ${CLIENT_NAME}  --instance-type ${CLIENT_INSTANCE_TYPE} --ebs=40  --aws-expire=12h || exit 1

echo "Uploading the Perseus"
aerolab files upload -c -n ${CLIENT_NAME} $PREFIX"/../client/templates/perseus_setup.sh" /root/perseus_setup.sh || exit 1
aerolab files upload -c -n ${CLIENT_NAME} $PREFIX"/../client/templates/perseus.sh" /root/perseus.sh || exit 1

echo "Uploading the configuration.yaml"
nip=$(aerolab cluster list -i |grep -A7 ${CLUSTER_NAME} | head -1 | grep -E -o 'int_ip=.{0,15}' | egrep -o '([0-9]{1,3}\.){3}[0-9]{1,3}' )

Perseus_Conf=$PREFIX"/../client/templates/perseus_configuration_template.yaml"
sed "s/_NAMESPACE_/${NAMESPACE}/g" ${Perseus_Conf} | sed "s/_IP_/${nip}/g" > configuration.yaml
aerolab files upload -c -n ${CLIENT_NAME} configuration.yaml /root/configuration.yaml || exit 1
rm -rf configuration.yaml

echo "Building Perseus"
aerolab client attach -n ${CLIENT_NAME} -l all --parallel -- bash /root/perseus_setup.sh
echo "Running Perseus"
aerolab client attach -n ${CLIENT_NAME} -l all --detach --parallel -- bash /root/perseus.sh

sleep 5

for (( i=1; i  <= ${CLIENT_NUMBER_OF_NODES}; i++ ))
  do
    echo ". "$PREFIX"/configure.sh\naerolab client attach -n "${CLIENT_NAME}" -l "${i}" -- tail -f out.log" > "term"${i}".sh"
    chmod 744 "term"${i}".sh"
    open -a iTerm "term"${i}".sh"
    sleep 3
    rm -f "term"${i}".sh"
  done

if [ ! -f $prefix"threads.yaml" ]; then
  cp $PREFIX"/../../src/main/resources/threads.yaml" $PREFIX"/."
fi