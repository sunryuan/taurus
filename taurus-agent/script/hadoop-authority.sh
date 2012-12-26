kinit -r 12l -k -t /home/$1/.keytab $1@DIANPING.COM;
if [ $? != 0 ] ; then
	exit 1
fi
kinit -R;