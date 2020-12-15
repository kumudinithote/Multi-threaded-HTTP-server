# Multi-threaded-HTTP-server
A Simple HTTP server where HTTP GET requests are used to request data from the specified source.


Instruction to Compile and run the Code:
  1. make
  2. java Server
  3. make clean

Brief:

Server class which contains main function and creates sockets and listen to the port 8080.
Server will accepts the TCP request using Socket and creates a new thread so that it can process each HTTP request requested.
Requested resource name, client port and IP address is then received by the server.
Server will print the number of times the resource is requested along with above details.

RequestHandler parse the http GET request. If the requested resource is found, then it populates the http response with 200 OK status.
Along with that it will populate the details like content length, content type, Last Modified Date, Date and Server Details.
If requested resource is not found then HTTP handler sends 404 Not Found response.


Implementation:

The directory called "www" is located in parent directory as your HTTP Server. WWW directory should contain some resources that server can serve.
If the Directory is not present then it will exit with the error message.
Successful execution of the requested resource, http server writes the output like requested resource, clients IP address and port number,
number of times the resource has been requested, Date and time of requested resource which is defined by RFC 7231 Date/Time Formats 2.


Sample Input/Output:

1. Start the Server

HTTPSERVER Details

Server Machine IP : remote02.cs.binghamton.edu
Server connection Port number: 8080
==========================================================
Starting HTTP server..

2. run the Wget command on client to get the resource.

  wget http://remote02.cs.binghamton.edu:8080/syllabus.pdf

3. If the input is executed then at server side we will get the following result

HTTP/1.0 200 OK
Date: Tue, 29 Sep 2020 06:19:22 GMT
Server: HTTP server/0.1
Last-Modified: Wed, 2 Sep 2020 22:12:24 GMT
Content-type: application/pdf
Content-Length: 115828


syllabus.pdf | 128.226.114.203 | 33800 | 1

4. If Incorrect resource is given to the server then it will show error DemoMessage
Input:  wget http://remote02.cs.binghamton.edu:8080/syllabus.pd
Output: syllabus.pd not found, this throws error 404..

5. If file is not found then 404 Not Found error is shown at client side.

