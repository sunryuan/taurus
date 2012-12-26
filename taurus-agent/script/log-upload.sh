log="hdfs://192.168.7.80:8020/user/workcron/taurus/logs/"
source /etc/profile;
source ~/.bash_profile;
kinit -r 12l -k -t /home/hadoop/.keytab hadoop@DIANPING.COM;
if [ $? != 0 ] ; then
        exit 1
fi
kinit -R;
tail -c 1000000 $1 >>$3
tail -c 100000 $2 >>$3
hadoop fs -moveFromLocal $3 $log$4
exit $?