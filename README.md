# Ordermanager

## Installation on raspberry3

### Install Raspberry

1. Download the [Raspberry pios lite](https://downloads.raspberrypi.org/raspios_lite_armhf_latest) from [here](https://www.raspberrypi.org/downloads/raspbian/)
2. Download and install https://www.balena.io/etcher/
3. Insert sd card and flash it by using image and balena-etcher
4. Put sd card in pi

### Use image

#### Read image of existing sd card
    
First find name of device with:

        diskutil list
For example: /dev/disk2

Then unmount the card:
    
        diskutil unmountDisk /dev/disk2

Now use this for creating the image:

        sudo dd if=/dev/rdisk2 bs=1m | gzip > /Users/joan/pi.gz
        
#### Write image to sdcard:

First find name of device with:

        diskutil list
For example: /dev/disk2

Now use this for writing the image:

        gzip -dc /Users/joan/pi.gz | sudo dd of=/dev/rdisk2 bs=1m

#### Add ssh key of machine on raspberry pi:
        
        ssh-copy-id pi@192.168.178.51
This will add the ssh keys of the mac to the raspberry.
See: https://www.raspberrypi.org/documentation/remote-access/ssh/passwordless.md

### Prepare Raspberry: Server
1. Enable ssh directly at raspberry with typing 
    
        sudo raspi-config
   
   Interface Options -> ssh-> yes
2. install java
       
        sudo apt update
        sudo apt upgrade
        sudo apt install default-jdk

#### Assign static ip address for wifi. 
Open file:

        sudo nano /etc/dhcpcd.conf

Insert the following block in case ip's start with 192.168.178.XXX otherwise adjust the ip:

        interface wlan0
        static ip_address=192.168.178.99/24
        static ip6_address=fd51:42f8:caae:d92e::ff/64
        static routers=192.168.178.1
        static domain_name_servers=192.168.178.1 8.8.8.8 fd51:42f8:caae:d92e::1
        
See: https://www.elektronik-kompendium.de/sites/raspberry-pi/1912151.htm
#### Copy jar file from mac to raspberry

1. Copy file to home dir of pi user via:

        scp ./target/order-manager-1.0.0.jar pi@<IP>:
    
#### Create systemd service

1. Copy service file

        scp ./raspberry/ordermanager.service pi@<IP>:ordermanager.service
1. ssh to pi.
1. make jar executable:
            
        chmod +x order-manager-1.0.0.jar
1. copy service file:
   
        sudo cp ordermanager.service /etc/systemd/system/ordermanager.service
1. start service:

        sudo systemctl start ordermanager.service
1. check state
    
        sudo systemctl status ordermanager.service
1. enable service so it gets started on boot

        sudo systemctl enable ordermanager.service
  
  
### Prepare Raspberry: UI
Um Chromium fullscreen zu starten ohne mauszeiger: 

#### Damit die Maus verschwindet
    
    apt-get install unclutter
installieren

#### Chrome im Vollbild ohne Maus bei Start öffnen

Folgendes in /etc/xdg/lxsession/LXDE-pi/autostart eintragen

        @unclutter -idle 0.1
        @xset s off
        @xset -dpms
        @xset s noblank
        @/home/pi/Desktop/ordermanager.sh
        
Auf dem Desktop Datei ordermanager.sh anlegen und mit:
        
        chmod +x /home/pi/Desktop/ordermanager.sh
        
ausführbar machen

Und folgendes einfügen

        #!/bin/bash
        sleep 10
        chromium-browser --incognito --kiosk http://192.168.178.47:8080/orders --autoplay-policy=no-user-gesture-required