#!/bin/bash
 java ${EQ_JAVA_OPTS:--Xmx256m} -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -Xloggc:./gc.log\
 -XX:+PrintGCDateStamps -XX:+CMSParallelRemarkEnabled -XX:ReservedCodeCacheSize=256m\
 -XX:+CMSScavengeBeforeRemark -jar target/visioneventquery-1.0.0-SNAPSHOT.jar