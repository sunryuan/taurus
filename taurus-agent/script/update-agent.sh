#!/bin/sh
# Update taurus agent

cd `dirname $0`
cd ..
set -e
#agent path
source /etc/profile
#kinit -r 12l -k -t ./conf/taurus.keytab taurus@DIANPING.COM;
#kinit -R


#hadoop fs -copyToLocal /user/workcron/taurus/agent/* ./
wget http://10.1.1.163:8000/taurus-agent.tar.gz
tar -xzvf  taurus-agent.tar.gz
rm taurus-agent.tar.gz

rm -f taurus-agent/conf/agentConf.properties
rm -f taurus-agent/script/agent-env.sh

rm -f ./lib/*

mv -f taurus-agent/lib/* ./lib/
mv -f taurus-agent/script/* ./script/
mv -f taurus-agent/conf/* ./conf/
mv -f taurus-agent/bin/* ./bin/

rm -rf taurus-agent

dos2unix bin/start.sh
dos2unix conf/agentConf.properties

chmod 744 bin/start.sh

bin/start.sh

#end