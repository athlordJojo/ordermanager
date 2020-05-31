# Ordermanager

## Installation on raspberry3

### Install Raspberry

1. Download the [Raspberry pios lite](https://downloads.raspberrypi.org/raspios_lite_armhf_latest) from [here](https://www.raspberrypi.org/downloads/raspbian/)
2. Download and install https://www.balena.io/etcher/
3. Insert sd card and flash it by using image and balena-etcher
4. Put sd card in pi

### Prepare Raspberry
1. Enable ssh directly at raspberry with typing 
    
        sudo raspi-config
   
   Interfaxce Options -> ssh-> yes
2. install java
       
        sudo apt update
        sudo apt upgrade
        sudo apt install default-jdk

### Copy jar file from mac to raspberry

1. Copy file to home dir of pi user via:

        scp ./target/order-manager-1.0.0.jar pi@<IP>:
    
### Create systemd service

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
  