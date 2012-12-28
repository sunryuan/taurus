#!/bin/sh
# Start taurus agent
# Run 'bin/start.sh init' for the first time. Run 'bin/start.sh for the others'
cd `dirname $0`
cd ..
if [ "$1" == "init" ]; then
	find ./script -type f | xargs dos2unix --dos2unix --safe
	chmod 744 script/*
	if [ "$2" == "production" ]; then
		sed -i 's/hadoopDirPro/hadoopDir/g' script/agent-env.sh
		sed -i 's/connectionStringPro/connectionString/g' conf/zooKeeper.properties
	elif  [ "$2" == "test" ]; then
		sed -i 's/hadoopDirTest/hadoopDir/g' script/agent-env.sh
		sed -i 's/connectionStringTest/connectionString/g' conf/zooKeeper.properties
	fi
else
	/usr/local/jdk/bin/java -classpath "conf/:lib/*"  com.dp.bigdata.taurus.agent.StartServer
fi
#end
