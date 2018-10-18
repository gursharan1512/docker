#!/bin/bash
## Incase of any error, setting option to exit from shell script
	 set -e 
##
docker-compose up -d
docker exec -it sharanghotra15121512_sparkApp_1 spark-submit --class test sparkJob/dockerRedisMysql2WithSaveToTextFile.jar
