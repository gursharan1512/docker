This repository contains installing docker and making containers with docker compose file.
This docker compose file creates 3 containers : redis, mysql and spark.
After the containers are build we run a jar file in which first we insert data to mysql, read data from mysql
& save data(which we read from mysql) to a file with spark and after that insert data to redis.