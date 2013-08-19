#useage: source agent-env.sh agent-env.sh

source /etc/profile
cd `dirname $1`
cd ..

for cmd in `cat conf/agentConf.properties`;
do
	if [[ $cmd != \#* ]]; then
	export $cmd;
	fi
done


#User-defined environment variables

#export WORMHOLE_INSTALL="/data/app/wormhole"
#export PATH=$PATH:$WORMHOLE_INSTALL/bin
