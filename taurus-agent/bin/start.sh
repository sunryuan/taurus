#!/bin/sh
# Start taurus agent
# Run 'bin/start.sh init' for the first time. Run 'bin/start.sh for the others'
cd `dirname $0`
cd ..
if [ $1 == 'init' ]; then
	find ./script -type f | xargs dos2unix --dos2unix --safe
	chmod 544 script/*
else
	/usr/local/jdk/bin/java -classpath "conf/:lib/*"  com.dp.bigdata.taurus.agent.StartServer
fi
#end
