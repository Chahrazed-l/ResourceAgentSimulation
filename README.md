# ResourceAgentSimulation

This java project allows reproducing a given human resource behaviour involved in business processes. More specifically, work organization , queuing behaviour and availability. 

# Getting Started
- For this project we used bonita community edition BPMS. So first of all, you need to download the latest version of BPMS (version: BonitaCommunity-7.8.2-Tomcat-8.5.38). To configure and run it, read carefully the instructions provided in the BPMS file. 
- you need to create an admin with a username=test and a password=test and install the process bar file (Pool-1.0.bar). 
- you need to create the user(s) profile(s) (present the resource(s) to be simulated) 
- The simulator is composed of two types of agents: 
    - The injector: responsible of creating process instances and assiging activities to the resources.
    - The agent resource: imitates the real resource in terms of work organization, queuing behaviour and availability. 
- The simulator takes as input information about the number of cases to instantiate each day and the specific behaviour of the resource(s) to be simulated. Examples of files are available in the configuration file. For example the configinject.csv is used by the the injector. Whereas the config.csv and the rest of the files are used as input for simulating 20 different resources. 
- Once finish, the simulator generates csv files containing information about the execution of the cases and the events.
- The CSV files are imported in a database, which will feed the Prom plugin to discover the behaviour reproduced by the agent. 



