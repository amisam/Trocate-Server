Introduction
============
In this sample, we are deploying a POJO after writing a services.xml and creating an aar. We can also test each method using the web browser

Pre-Requisites
==============

Apache Ant 1.6.2 or later

Building the Service
====================

Type "ant generate.service" or just "ant" from directory. It will place the service implementation in the build directory (build/ServerFacadeService.aar).

Deploying the Service
=====================

Deploy the build/ServerFacadeService.aar by copying it into repository/services.

Executing the Service
=====================

Start the server usign the following command.

"/vol/courses/nwen304/2011/axis2-1.5.4/bin/axis2server.sh -repo ~/repository"

Note that -repo refers to where you have placed your repository. (in this case we placesed it in repository)

For testing using the web broweser you can just run the above line in the command shell. However for use of the server over the internet you will need to run the above command on a server such as greta-pt. You can navigate here with the command "ssh greta-pt"

Running the Client
==================
- From your browser, If you point to the following URL:
"http://localhost:8081/axis2/services/ServerFacade/createUser?name={someString}&password={someString}"
if you are the first one to do so you will get the following responce
<ns:createUserResponse>
  <ns:return>0</ns:return>
</ns:createUser>

from there you can change the url slightly from createUser to logon and provided the inputs are the same will log you in with the 0 returned to you again

To see more documentation run the server and type into the webexplorer 

You will execute the main method of the class. You should see the same effect as running the main method from the command line.

When given output like this it becomes a job for a parser.