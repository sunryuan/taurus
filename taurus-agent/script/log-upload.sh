#!/bin/sh
# Upload taurus attempt executing logs
# Discarded

cd `dirname $0`

source /etc/profile;
source ~/.bash_profile;
source ./agent-env.sh ./agent-env.sh

kinit -r 12l -k -t ../conf/taurus.keytab taurus@DIANPING.COM;
if [ $? != 0 ] ; then
        exit 1
fi
kinit -R;

echo '<html><head>    <meta http-equiv="content-type" content="text/html; charset=UTF-8">    <title>log</title>  
	<style type="text/css"></style><style>.stderr {background-color: #f5ebeb;}.stdout {background-color: #f5ebeb;}
	body, table td, select {font-family: Arial Unicode MS, Arial, sans-serif;font-size: medium;}</style></head>
	<body><div><div> <h1>StdErr</h1> </div> <div class="stderr">' >>$3
tail -c 1000000 $2 |  sed -e "s/$/&<br\/>/g" >>$3
echo '</div> <div> <h1>StdOut</h1> </div> <div class="stdout">' >>$3
tail -c 100000 $1 |  sed -e "s/$/&<br\/>/g" >>$3
echo '</div></div></body></html>' >>$3
hadoop fs -moveFromLocal $3 $taurusLog$4
exit $?

# end