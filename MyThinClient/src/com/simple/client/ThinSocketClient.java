package com.simple.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThinSocketClient {
    private char clientName;
    private static int count=64;                       //used to generate the alphabetic client name 
    
    // TCP Components
    private Socket socket=null;
    private BufferedReader inStream=null;
    private PrintWriter outStream=null;
    private int portNum=-1;
    private String hostName=null;
    private int timeOut=0;

   //*********************************** Main Method ********************

    public static void main(String args[]) {
    	int clientCount = 3;
    	ExecutorService es = Executors.newFixedThreadPool(clientCount);
    	for(int i=0; i<clientCount; i++){
    		es.execute(new Runnable(){
				@Override
				public void run() {
					new ThinSocketClient();
				}
    			
    		});
    	} 
    	es.shutdown();
    }
    
   //*********************************** Constructor ********************
    public ThinSocketClient() 
    {    	
    	//Auto generate client name. 
    	++count; 
    	this.clientName=(char)count; 
    	    	
    	//System.out.println("ClientName:"+ clientName);
    	
    	//Read the properties file to get the connection info to the server and other preferences 
		ReadProperties rp = new ReadProperties();
		rp.loadFile("config.properties");
		
		portNum = Integer.valueOf(rp.getPropertyValue("portNum"));
	    hostName = (rp.getPropertyValue("hostName")).trim();
	    timeOut = Integer.valueOf(rp.getPropertyValue("timeOut"));
	    
	    if(portNum<0 || portNum>65535)
	    {
	    	System.out.println("Please supply a valid port number");    
	    	return;
	    }
	    
	    if(hostName==null || hostName=="")
	    {
	    	System.out.println("Please supply a valid host name or IP address");    
	    	return;
	    }
	    
	    try
	    {
		    //Establish connection to the server 
	        openSocket();
	        
	        // If everything has been initialized then we want to read/write some data to/from the socket 
	         if (socket != null && outStream != null && inStream != null)
	         {
	        	 for(int i=1;i<21;i++)
	        	 {//send the id and counter to the server. Loop for 20 times. 
	        	    runClient(i);
	        	    TimeUnit.SECONDS.sleep(1); //add a second of delay between requests 
	        	 }
	        	 //indicate end of task to the server 
	        	 endTask();
	         }
	    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {        	 
        	 try{
        		 if(outStream !=null)
            		 outStream.close();
        		 if(inStream !=null)
        			 inStream.close();
        	 	 if(socket !=null)
        	 		 socket.close();
        	 }catch (IOException e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}
		}          
    }//end constructor 

    //*********************************** Method to establish connection ********************
    private void openSocket() 
    {
        try
        {
           System.out.println("Clien"+clientName + " is trying to connect with server:"+ hostName+ " at port:"+portNum);

           // create socket
           InetAddress inetAddress = InetAddress.getByName(hostName);
            if (!inetAddress.isReachable(timeOut*1000))
            {
                System.out.println("Error! Unable to connect with server.\nServer IP Address may be wrong.");
                System.exit(1);
            }
     
            socket = new Socket(hostName, portNum);
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new PrintWriter(socket.getOutputStream(), true);
            
        } catch (UnknownHostException e){
        	System.err.println("Don't know about host:"+hostName);
        } catch (SocketException e) {
            System.out.println("Socket Exception:\n" + e);          
        } catch (IOException e) {
        	System.err.println("Couldn't get I/O for the connection to"+hostName);            
        }
    }//end method openSocket 
       
    //*********************************** Method to indicate end of task to the server ********************
    public void endTask() {
        try { 	    
        	System.out.println("Client" + clientName+ " is done");
        	outStream.println(".");
        	outStream.println(clientName);
                                
        	//The response from server is probably redundant. Used it to do testing
            String response=null; 
            while (response==null) {
                response = inStream.readLine();
                //System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Client is closed...");
        }
    }//end method endTask

    //*********************************** Method to transfer data and read response from server ********************
    public void runClient(int count) {
        try { 	    
        	outStream.println(clientName);
            outStream.println(count);  
                       
            String response=null; 
            while (response==null) {
                response = inStream.readLine();
                //System.out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Client is closed...");
        }
    }//end method runClient    

}//end class ThinSocketClient