#!/bin/sh
# Deploy taurus executing files
# Discarded

cd `dirname $0`
function extract () {
   path=`dirname $1`
   if [ -f $1 ] ; then
       case $1 in
        *.tar.bz2)      tar xvjf $1 -C $path&&rm -f $1;;
        *.tar.gz)       tar xvzf $1 -C $path&&rm -f $1;;
        *.bz2)          bunzip2 $1 && rm -f $1;;
        *.gz)           gunzip $1  && rm -f $1;;
        *.tar)          tar xvf $1  -C $path&&rm -f $1;;
        *.zip)          unzip $1 -d $path&&rm -f $1;;
#       *.tar.xz)       tar Jxvf $1 -C $path&&rm -f $1;;
#       *.rar)          unrar x $1 && rm -f $1;;        
#       *.tbz2)         tar xvjf $1 ;;
#        *.tgz)          tar xvzf $1 ;;
#       *.Z)            uncompress  ;;
#       *.7z)           7z x $1     ;;
        *)              echo "don't know how to extract '$1'..." ;;
       esac
   else
       echo "'$1' is not a valid file!"
   fi
}

source /etc/profile;
source ~/.bash_profile;
kinit -r 12l -k -t ../conf/taurus.keytab taurus@DIANPING.COM;
kinit -R;
path=`dirname $2`
if [ -f $path ] ; then
        rm -rf $path
fi
mkdir -p $path
hadoop fs -copyToLocal $1 $2
extract $2
exit $?
# end 