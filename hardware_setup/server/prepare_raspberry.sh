#!/bin/bash
ipadress=192.168.178.99

echo "192.168.178.99  appsrv" | ssh pi@$ipadress "sudo tee -a /etc/hosts"