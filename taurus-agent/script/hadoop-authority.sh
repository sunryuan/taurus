kinit -r 12l -k -t /home/$USER/.keytab $USER@DIANPING.COM;
if [ $? != 0 ] ; then
	exit 1
fi
kinit -R;