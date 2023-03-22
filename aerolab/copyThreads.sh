if [ -f "configure.sh" ]; then
    prefix=""
fi

if [ -f "../configure.sh" ]; then
    prefix="../"
fi

. $prefix"configure.sh"

aerolab files upload -c -n ${CLIENT_NAME} $prefix"threads.yaml" /root/threads.yaml || exit 1