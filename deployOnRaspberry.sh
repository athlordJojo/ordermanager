#!/bin/bash
ipadress=192.168.178.99

ssh pi@$ipadress "sudo systemctl stop ordermanager.service"
scp target/order-manager-1.0.0.jar pi@$ipadress:/home/pi/order-manager-1.0.0.jar
ssh pi@$ipadress "sudo systemctl start ordermanager.service"