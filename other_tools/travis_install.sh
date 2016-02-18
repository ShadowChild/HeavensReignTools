#!/usr/bin/env bash

function installSC() {
  
  cd ./Cybernize
  mvn clean install -DskipTests=true
  cd ..
}

function installSE() {
  
  cd ./SilenceEngine
  gradle clean build javadoc
  mvnInstallSE
  cd ..
}

function mvnInstallSE() {
  
  mvn install:install-file -Dfile=./build/libs/SilenceEngine.jar -DgroupId=com.goharsha -DartifactId=SilenceEngine -Dversion=0.4.1b -Dpackaging=jar
}

function makeDirs() {
  
  mkdir ./libs
  mkdir ./libs/SilenceEngine
}

function copyLibs() {
  
  cp ./SilenceEngine/build/libs/SilenceEngine.jar ./libs/SilenceEngine/SilenceEngine.jar  
}

installSC
installSE
makeDirs
copyLibs
