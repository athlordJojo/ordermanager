
#### (NOT USED ANYMORE) Read image of existing sd card
    
First find name of device with:

        diskutil list
For example: /dev/disk2

Then unmount the card:
    
        diskutil unmountDisk /dev/disk2

Now use this for creating the image:

        sudo dd if=/dev/rdisk2 bs=1m | gzip > /Users/joan/pi.gz
        
#### (NOT USED ANYMORE) Write image to sdcard:

First find name of device with:

        diskutil list
For example: /dev/disk2

Now use this for writing the image:

        gzip -dc /Users/joan/pi.gz | sudo dd of=/dev/rdisk2 bs=1m
        
        
### (NOT USED ANYMORE) Install Fresh Raspberry

1. Download the [Raspberry pios lite](https://downloads.raspberrypi.org/raspios_lite_armhf_latest) from [here](https://www.raspberrypi.org/downloads/raspbian/)
2. Download and install https://www.balena.io/etcher/
3. Insert sd card and flash it by using image and balena-etcher
4. Put sd card in pi        