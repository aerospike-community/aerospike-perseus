. configure.sh

aerolab client create ams -n ${CLIENT_NAME} -s ${CLUSTER_NAME}  --instance-type ${AWS_CLIENT_INSTANCE} --ebs=40 --secgroup-id=${SEC_GROUP} --subnet-id=${SUBNET} || exit 1

aerolab files upload -c -n ${CLIENT_NAME} perseus.sh /root/perseus.sh || exit 1

nip=$(aerolab cluster list -j |grep -A7 ${CLUSTER_NAME} |grep IpAddress |head -1 |egrep -o '([0-9]{1,3}\.){3}[0-9]{1,3}')
sed "s/_NAMESPACE_/${NAMESPACE}/g" templates/configuration_template.yaml | sed "s/_IP_/${nip}/g" > configuration.yaml
aerolab files upload -c -n ${CLIENT_NAME} configuration.yaml /root/configuration.yaml || exit 1
rm -rf configuration.yaml

grafana=$(aerolab client list -j |grep -A7 ${CLIENT_NAME} |grep PublicIp |head -1 |egrep -o '([0-9]{1,3}\.){3}[0-9]{1,3}')
open "http://${grafana}:3000"

aerolab client attach -n ${CLIENT_NAME} -- bash /root/perseus.sh
