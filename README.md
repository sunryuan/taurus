Overview
========
Taurus is a scheduling system which will schedule tasks according crontab and dependency expression.

Get Started
-----------

Installation:
    git clone https://github.com/dp-bigdata/Taurus.git

    mvn install

    cd taurus-restlet

    cp target/taurus-restlet.tar.gz ( to somewhere )

    tar -xzvf tarusu-restlet.tar.gz

Configure:
* change conf/restlet.properties according to your system,	
  Normaly, "localpath" and "dp.hdfsclinet.keytab.file" have to change	
* ask for a keytab.file, and put it into conf directory	

Run:
    ./script/taurus-start.sh standalone | all
'standalone' mode only starts restlet server, while the 'all' mode starts both engine server and restlet server.


Copyright and license
---------------------
Copyright 2012 DianPing, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
