if [ -z "$PREFIX" ];
  then
    PREFIX=$(pwd "$0")"/"$(dirname "$0")
    . $PREFIX/configure.sh
fi

. $PREFIX/../grafana/destroy.sh
