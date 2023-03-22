if [ -f "configure.sh" ]; then
    prefix=""
fi

if [ -f "../configure.sh" ]; then
    prefix="../"
fi

. $prefix"configure.sh"

aerolab cluster destroy -f -n ${CLUSTER_NAME}


