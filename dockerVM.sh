#!/bin/bash
## Incase of any error, setting option to exit from shell script
	 set -e 
##
VMName='docker-101'
VMZone='us-central1-b'
echo "Creating VM instance "
gcloud compute instances create $VMName --zone $VMZone --tags http-server,https-server --scopes cloud-platform

gsutil cp -r "gs://cdp-gcp-testing/dockerFiles" .
cd "dockerFiles"
gcloud compute scp --zone $VMZone docker-compose.yml $VMName:~/
gcloud compute scp --zone $VMZone dockerSetup.sh $VMName:~/
gcloud compute scp --zone $VMZone dockerContainers.sh $VMName:~/
gcloud compute ssh --zone $VMZone $VMName --command 'mkdir dockerVol'
gcloud compute scp --zone $VMZone dockerRedisMysql2WithSaveToTextFile.jar $VMName:~/dockerVol/

gcloud compute ssh --zone $VMZone $VMName --command 'chmod +x dockerSetup.sh && chmod +x dockerContainers.sh && sudo apt-get install dos2unix && dos2unix * && ./dockerSetup.sh'
gcloud compute ssh --zone $VMZone $VMName --command './dockerContainers.sh'
cd ~
rm -r dockerFiles