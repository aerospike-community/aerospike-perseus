if [ -z "$PREFIX" ];
  then
    PREFIX=$(pwd "$0")"/"$(dirname "$0")
    . $PREFIX/configure.sh
fi

aerolab files upload -c -n ${CLIENT_NAME} $PREFIX"/threads.yaml" /root/threads.yaml || exit 1