#!/bin/bash
cd `dirname $0`
SCRIPTDIR=`pwd`
cd -
gradlew setupDevWorkspace setupDecompWorkspace assemble
read -n1 -r -p "Press any key to continue..." key