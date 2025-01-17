echo "Adding Grafana"
aerolab client create ams -n ${GRAFANA_NAME} -s ${CLUSTER_NAME} --instance-type ${GRAFANA_INSTANCE_TYPE} --ebs=40 --aws-expire=${AWS_EXPIRE} || exit 1

grafana=$(aerolab client list -i | grep -A7 ${GRAFANA_NAME} | head -1 | grep -E -o 'ext_ip=.{0,15}' | egrep -o '([0-9]{1,3}\.){3}[0-9]{1,3}' )

open "http://${grafana}:3000"
