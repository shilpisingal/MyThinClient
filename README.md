# MyThinClient
# Make sure the server is started before you run the client 
# Update config.properties file and provide the following parameters 
  1) port Number of the server 
  2) hostName or IP address of the server 
  3) timeOut value for client in seconds 
# Now run the client. ThinSocketClient.java provides a main method. The main method creates 3 threads, each invoking the constuctor of class. 
You can re-write the main as desired. The program generates a unique alphabetically increasing (A, B,C...)  client name for each instance.
Hence I wrote the main method like this so you can see a distinct client name. 
