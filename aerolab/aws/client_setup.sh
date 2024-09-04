if [ -z "$PREFIX" ];
  then
    PREFIX=$(pwd "$0")"/"$(dirname "$0")
    . $PREFIX/configure.sh
fi

. $PREFIX/../client/setup.sh
. $PREFIX/../client/buildPerseus.sh
. $PREFIX/../client/runPerseus.sh