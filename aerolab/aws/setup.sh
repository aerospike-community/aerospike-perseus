PREFIX=$(pwd "$0")"/"$(dirname "$0")
. $PREFIX/configure.sh

. $PREFIX/clusterSetup.sh
. $PREFIX/clientSetup.sh
. $PREFIX/grafanaSetup.sh
