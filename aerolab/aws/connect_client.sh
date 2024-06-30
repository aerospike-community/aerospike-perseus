if [ -z "$PREFIX" ];
  then
    PREFIX=$(pwd "$0")"/"$(dirname "$0")
    . $PREFIX/configure.sh
fi

aerolab client attach -n ${CLIENT_NAME} -l $1 -- bash