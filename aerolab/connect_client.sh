if [ -f "configure.sh" ]; then
    prefix=""
fi

if [ -f "../configure.sh" ]; then
    prefix="../"
fi

. $prefix"configure.sh"

aerolab client attach -n ${CLIENT_NAME} -l $1 -- bash