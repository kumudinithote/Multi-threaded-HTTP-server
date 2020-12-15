all: httpServer

httpServer: server.java
	javac *.java

run: httpServer
	java server

clean:
	rm *.class
