#!/bin/sh
# Start taurus agent
# Run 'bin/start.sh init' for the first time. Run 'bin/start.sh for the others'
cd `dirname $0`
cd ..

#agent path
for cmd in `cat conf/agentConf.properties`;
do
	if [[ $cmd != \#* ]]; then
	export $cmd;
	fi
done

if [ "$1" == "init" ]; then
	find ./script -type f | xargs dos2unix --dos2unix --safe
	chmod 744 script/*
	mkdir -p $taurusJobPath/running
	chmod 777 $taurusJobPath/running
	mkdir -p $taurusJobPath/hadoop
	chmod 777 $taurusJobPath/hadoop
else
	/usr/local/jdk/bin/java -classpath "conf/:lib/*"  com.dp.bigdata.taurus.agent.StartServer
fi
#end
