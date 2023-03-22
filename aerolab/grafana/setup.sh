if [ -f "configure.sh" ]; then
    prefix=""
fi

if [ -f "../configure.sh" ]; then
    prefix="../"
fi

. $prefix"configure.sh"


# exporter
echo "Adding exporter"
aerolab cluster add exporter -n ${CLUSTER_NAME} -o $prefix"grafana/templates/ape.toml"

echo "Adding Grafana"
aerolab client create ams -n ${GRAFANA_NAME} -s ${CLUSTER_NAME} --instance-type ${GRAFANA_INSTANCE_TYPE} --ebs=40 || exit 1

grafana=$(aerolab client list -j | grep -A7 ${GRAFANA_NAME} | grep PublicIp | head -1 | egrep -o '([0-9]{1,3}\.){3}[0-9]{1,3}')
open "http://${grafana}:3000"
