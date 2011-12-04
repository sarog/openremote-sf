This is a simple rest interface we are using here is the documentation 

http://www.universal-devices.com/mwiki/index.php?title=ISY-99i_Series_INSTEON:REST_Interface 



Below is a simple unix script with perl to turn on and off the lights 

#!/bin/sh 
export SwitchGateWay="192.168.41.12"

#we pass %20 as the space char on the cmd line 
#so there are no issues with spaces 

export SwitchAddress="17%2054%20AE%201"


#Turn switch off 
wget -q -O - "$@"--user=admin --password=admin http://$SwitchGateWay/rest/nodes/$SwitchAddress/cmd/DOF
sleep 10	
wget -q -O - "$@"--user=admin --password=admin http://$SwitchGateWay/rest/nodes/$SwitchAddress/cmd/DON
#Turn switch on
sleep 10	
wget -q -O - "$@"--user=admin --password=admin http://$SwitchGateWay/rest/nodes/$SwitchAddress/cmd/DON/0
sleep 10	
wget -q -O - "$@"--user=admin --password=admin http://$SwitchGateWay/rest/nodes/$SwitchAddress/cmd/DON/1
sleep 10	
wget -q -O - "$@"--user=admin --password=admin http://$SwitchGateWay/rest/nodes/$SwitchAddress/cmd/DON/128
sleep 10	
wget -q -O - "$@"--user=admin --password=admin http://$SwitchGateWay/rest/nodes/$SwitchAddress/cmd/DON/128
sleep 10	

#Turn switch off
wget -q -O - "$@"--user=admin --password=admin http://$SwitchGateWay/rest/nodes/$SwitchAddress/cmd/DOF

