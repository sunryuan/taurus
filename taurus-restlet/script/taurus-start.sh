echo " Usage: ./taurus-start.sh standalone | all "
if [ "$1" == "all" ];then
    java -cp "./conf:lib/*" com.dp.bigdata.taurus.restlet.TaurusServer all
elif [ "$1" == "standalone" ];then
    java -cp "./conf:lib/*" com.dp.bigdata.taurus.restlet.TaurusServer standalone
fi
    
