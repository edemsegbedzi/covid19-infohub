#!/usr/bin/env bash
ssh javatar@144.91.101.249 "cd /home/javatar/apps/mindit/mindit-covid19-rancard; git pull origin master;fuser -k 9095/tcp;mvn clean package; nohup java -jar target/mindITCovid19.jar 1>/dev/null 2>/dev/null & exit"
