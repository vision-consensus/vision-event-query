#!/bin/bash
while true; do
  pid=`ps -ef | grep visioneventquery | grep -v grep | awk '{print $2}'`
  if [ -n "$pid" ]; then
    kill -15 $pid
    echo "ending vision event query process"
    sleep 1
  else
    echo "vision event query killed successfully!"
    break
  fi
done
total=`cat /proc/meminfo  |grep MemTotal |awk -F ' ' '{print $2}'`
xmx=`echo "$total/1024/1024*0.6" | bc |awk -F. '{print $1"g"}'`
logtime=`date +%Y-%m-%d_%H-%M-%S`
 nohup java -Xmx$xmx -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -Xloggc:./gc.log\
 -XX:+PrintGCDateStamps -XX:+CMSParallelRemarkEnabled -XX:ReservedCodeCacheSize=256m\
 -XX:+CMSScavengeBeforeRemark -jar target/visioneventquery-1.0.0-SNAPSHOT.jar >> query.log 2>&1 &

sleep 10
echo "ok!"
