if [ -z "$PREFIX" ];
  then
    PREFIX=$(pwd "$0")"/"$(dirname "$0")
    . $PREFIX/configure.sh
fi

echo $PREFIX

. $PREFIX/clientDestroy.sh
. $PREFIX/grafanaDestroy.sh
. $PREFIX/clusterDestroy.sh
