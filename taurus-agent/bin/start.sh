#!/bin/sh
# Start taurus agent

cd `dirname $0`
cd ..

#agent path
for cmd in `cat conf/agentConf.properties`;
do
	if [[ $cmd != \#* ]]; then
	export $cmd;
	fi
done

#create and modify directory
find ./script -type f | xargs dos2unix --dos2unix --safe
chmod 744 script/*
mkdir -p $taurusJobPath/running
chmod 777 $taurusJobPath/running
mkdir -p $taurusJobPath/hadoop
chmod 777 $taurusJobPath/hadoop

#kill old agent
id=`ps -ef | grep com.dp.bigdata.taurus.agent.StartServer | grep -v grep | awk '{print $2}'`
if [ "$id" != "" ]; then
	echo "An agent has already started. It will be killed."
	kill $id
fi

#start agent
export KRB5_CONFIG=./conf/krb5.conf

nohup /usr/local/jdk/bin/java  -Djava.security.krb5.realm=DIANPING.COM -Djava.security.krb5.kdc=10.2.6.103:10.2.6.152 -classpath "conf/:lib/*"  com.dp.bigdata.taurus.agent.StartServer >/dev/null &
echo "start success"
#end
