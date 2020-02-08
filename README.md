# JLDFS
Implementation of a naive distributed file system

# Design

## Client

Class `JDLFS` implemented here. `JDLFS` can be mounted to the local directory for access. An `FS` object contains the configuration list for the **Master** and **Slave** nodes. When it is initialized, it would only connect to master.

## MasterServer

MasterServer maintains the same configuration with the client node. The metadata is stored(inode info, replica info). It can send the files to serveral slaveserver for physical storage.

## SlaveServer
Slave Server connects to Master. When the writen is done, it would return a signal to the master. 