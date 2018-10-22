This repository contains installing docker and making containers with docker compose file.

This docker compose file creates 3 containers : redis, mysql and spark.

After the containers are build we run a jar file in which first we insert data to mysql, read data from mysql
& save data(which we read from mysql) to a file with spark and after that insert data to redis.

### Script files
* dockerVM 			-	script to create VM in GCP and run dockerSetup.sh file in VM
* dockerSetup		-	script to install docker and docker compose in ubuntu

Scripts folder contains files for setting up docker in GCP VM and create containers
* Upload dockerFiles file in GCP bucket
* Change creatingVM.cfg file according to your project
* Now upload creatingVM.cfg and dockerVM.sh file in cloud shell and run sh file

To see detailed docker installation check out the below link : 

https://tortuemat.gitlab.io/blog/2017/05/20/Docker-on-Google-Cloud-Platform-1/
