#!/bin/sh
# Renew hadoop ticket cache
cd `dirname $0`
source ./agent-env.sh
kinit -r 12l -k -t $homeDir/$1/.keytab $1@DIANPING.COM;
if [ $? != 0 ] ; then
	exit 1
fi
kinit -R;
# end