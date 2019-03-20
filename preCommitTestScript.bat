#!/bin/sh

#pre commit git hook which calls java AutoVersionInc.jar to parse buil.gradle and increase versions automatically
#to call jar file we need full path and for build gradle also need full path
java -jar F:\\netbeansPorjects\\AutoVersionInc\\someInnerProjectTest\\AutoVersionInc.jar F:\\netbeansPorjects\\AutoVersionInc\\someInnerProjectTest\\build.gradle versionBuild aztelekomVersionCode
STATUS="${?}"
#echo "${STATUS}"
if [ "${STATUS}" == "0" ]; 
then
	echo "pre commit hook fibished successfully. congrats"
	exit 0
else
  echo "Version increasd failed :("
  echo "Aborting the commit! Run with --no-verify if you want to ignore pre commit hook."
  exit 1
fi
