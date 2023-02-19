# Messenger

## Contents
* [General](#general-info)
* [Technologies](#technologies)
* [Installation](#installation)
  * [Ubuntu](#installation-ubuntu)
  * [Windows](#installation-windows)
* [Start the Program](#start-program)

## General <a name="general-info"></a>
This is a trial messenger for a small number of participants. To use the tool you need to create a new file **_Сonfig.java_** or update the current one with credentials for the server, e.g. file path for DB to store logged user data, host, port, output file path for logging and error handling.

Several options are implemented for communication:
- "global" to send a message to all participants;
- "online" to check if the user is online;
- "msg" to send a message to a specific user; 
- "login" and "reg" to log in and register, respectively.

***
## Technologies <a name="technologies"></a>

A list of technologies used for the java-based project:
* OpenJDK JRE 11.0.17
* Telnet

***
## Installation <a name="installation"></a>
### Ubuntu <a name="installation-ubuntu"></a>

- Keep your environment up to date
```
$ sudo apt-get update
$ sudo apt-get upgrade
```

- Install OpenJDK JRE
```
$ sudo apt install default-jre
$ java -version
```

- Install telnet
```
$ sudo apt install telnetd telnet
```
### Windows <a name="installation-windows"></a>
- Install [Java](https://www.java.com/download/ie_manual.jsp)
- Enable the Telnet Client

***
## Start the program <a name="start-program"></a>
```
$ telnet localhost 8080
```
The next steps:

```
$ reg user password
```

```
$ login user password
```
