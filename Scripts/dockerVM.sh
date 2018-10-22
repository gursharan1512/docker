#!/bin/bash
## Incase of any error, setting option to exit from shell script
	 set -e 
##

######################### Reading variables #########################
source creatingVM.cfg

######################### Creating VM instance #########################
echo "Creating VM instance "
gcloud compute instances create $VMName --zone $VMZone --tags http-server,https-server --scopes cloud-platform

######################### Reading files from bucket #########################
gsutil cp -r "gs://"$bucket"/dockerFiles" .
cd "dockerFiles"
gcloud compute scp --zone $VMZone docker-compose.yml $VMName:~/
gcloud compute scp --zone $VMZone dockerSetup.sh $VMName:~/
gcloud compute scp --zone $VMZone dockerContainers.sh $VMName:~/
gcloud compute ssh --zone $VMZone $VMName --command 'mkdir dockerVol'
gcloud compute scp --zone $VMZone dockerRedisMysql2WithSaveToTextFile.jar $VMName:~/dockerVol/

######################### Running dockerSetup.sh script #########################
gcloud compute ssh --zone $VMZone $VMName --command 'chmod +x dockerSetup.sh && sudo apt-get install dos2unix && dos2unix * && ./dockerSetup.sh'
gcloud compute ssh --zone $VMZone $VMName --command 'docker-compose up -d'

######################### Removing extra files from cloud shell #########################
cd ~
rm -r dockerFiles
rm dockerVM.sh