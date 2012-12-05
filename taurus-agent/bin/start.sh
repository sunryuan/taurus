export taurusJobPath=/data/app/taurus/jobs/
export taurusAgent=/data/app/taurus/
cd $taurusAgent
/usr/local/jdk/bin/java -classpath "conf/:lib/*"  com.dp.bigdata.taurus.agent.StartServer