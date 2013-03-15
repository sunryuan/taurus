#useage: source agent-env.sh
source /etc/profile
cd `dirname $1`
cd ..
for cmd in `cat conf/agentConf.properties`;
do
	if [[ $cmd != \#* ]]; then
	export $cmd;
	fi
done


#path in host
#export WORMHOLE_INSTALL="/data/app/wormhole"
#export PATH=$PATH:$WORMHOLE_INSTALL/bin
