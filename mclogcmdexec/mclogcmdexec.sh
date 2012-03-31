#!/bin/bash

java -cp mclogcmdexec.jar:/home/minecraft/bin/lib/commons-logging-1.0.4.jar:/home/minecraft/bin/lib/commons-lang-1.0.1.jar org.titanomachia.mclogcmdexec.MCLogCommandExecutor nohup.out&
echo $! > mclogcmdexec.pid

