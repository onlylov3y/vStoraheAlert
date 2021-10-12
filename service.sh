#!/bin/bash

param=$1

if [ -z "$param" ]
then
    #Storage Alert Service. Helper
    java -jar StorageAlert.jar --help
elif [ $param == 'run' ] || [ $param == 'console' ]
then
    echo "Storage Alert Service. Running..."
    java -Djava.security.egd=file:///dev/erandom -jar StorageAlert.jar --push
elif [ $param == 'start' ]
then
    echo "Storage Alert Service. Starting..."
    sudo systemctl start storage-alert
    #sudo systemctl status storage-alert
elif [ $param == 'restart' ]
then
    echo "Storage Alert Service. Restarting..."
    sudo systemctl restart storage-alert
    #sudo systemctl status storage-alert
elif [ $param == 'stop' ]
then
    echo "Storage Alert Service. Stopping..."
    sudo systemctl stop storage-alert
    #sudo systemctl status storage-alert
elif [ $param == 'export' ]
then
    echo "Export Report Service. Running..."
    java -Djava.security.egd=file:///dev/erandom -jar StorageAlert.jar --export
elif [ $param == 'install' ]
then
    echo "Storage Alert Service. Installing..."
    sudo cp config/storage-alert.service  /etc/systemd/system/storage-alert.service
    sudo systemctl daemon-reload
    sudo systemctl enable storage-alert.service
else
    java -jar StorageAlert.jar --help
fi
