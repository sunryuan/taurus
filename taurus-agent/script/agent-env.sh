
source /etc/profile
#keytab file path set
export homeDir='/home/'

#hadoop path
export hadoopDir='hdfs://10.2.6.102:8020' #production #default
#export hadoopDir='hdfs://192.168.7.80:8020' #test
export taurusLog="${hadoopDir}/user/workcron/taurus/logs/"

#task type
export WORMHOLE_INSTALL="/data/app/wormhole"
export PATH=$PATH:$WORMHOLE_INSTALL/bin
