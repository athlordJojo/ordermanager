#!/bin/bash
ipadress=192.168.178.51
scp ordermanager.sh pi@$ipadress:/home/pi/Desktop/ordermanager.sh
ssh pi@$ipadress chmod +x /home/pi/Desktop/ordermanager.sh

# disable update notification. https://www.raspberrypi.org/forums/viewtopic.php?f=63&t=264399&start=50
scp disableupdatenotification.sh pi@$ipadress:/home/pi/disableupdatenotification.sh
ssh pi@$ipadress chmod +x /home/pi/disableupdatenotification.sh
ssh pi@$ipadress "sh /home/pi/disableupdatenotification.sh"


ssh pi@$ipadress "sudo apt-get install unclutter -y"
cat autostartcontent.txt | ssh pi@$ipadress "sudo tee -a /etc/xdg/lxsession/LXDE-pi/autostart"