#!/bin/bash
## Incase of any error, setting option to exit from shell script
	 set -e 
##
userId='sharanghotra15121512'
jarFile='dockerRedisMysql2WithSaveToTextFile.jar'
docker exec -it $userId'_sparkApp_1' spark-submit --class test 'sparkJob/'$jarFile
