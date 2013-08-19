#!/bin/sh
# Renew hadoop ticket cache
# Discarded

cd `dirname $0`
source ./agent-env.sh ./agent-env.sh
sudo -u $1 -i "kinit -r 12l -k -t $homeDir/$1/.keytab $1@DIANPING.COM";
if [ $? != 0 ] ; then
	exit 1
fi
sudo -u $1 -i "kinit -R";

# end