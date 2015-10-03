package MultiPersonChatApplication;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
public class MultiThreadChatClient implements Runnable{

	
	  private static Socket clientSocket = null;
	  
	  private static PrintStream output = null;
	  
	  private static DataInputStream data = null;

	  private static BufferedReader inputline= null;
	  private static boolean closed = false;
	public static void main(String[] args) {
		
	    int portNumber = 40000;
	    
	    String host = "127.0.0.1";

	    if (args.length < 2) {
	      System.out
	          .println(" java MultiThreadChatClient <host> <portNumber>\n"
	              + "Now using host=" + host + ", portNumber=" + portNumber);
	    } else {
	      host = args[0];
	      portNumber = Integer.valueOf(args[1]).intValue();
	    }

	    
	    try {
	      clientSocket = new Socket(host, portNumber);
	      inputline = new BufferedReader(new InputStreamReader(System.in));
	      output = new PrintStream(clientSocket.getOutputStream());
	      data = new DataInputStream(clientSocket.getInputStream());
	    } catch (UnknownHostException e) {
	      System.err.println("Don't know about host " + host);
	    } catch (IOException e) {
	      System.err.println("Couldn't get I/O for the connection to the host "
	          + host);
	    }

	    
	    if (clientSocket != null && output != null && data != null) {
	      try {

	        
	        new Thread(new MultiThreadChatClient()).start();
	        while (!closed) {
	          output.println(inputline.readLine().trim());
	        }
	        
	        output.close();
	        data.close();
	        clientSocket.close();
	      } catch (IOException e) {
	        System.err.println("IOException:  " + e);
	      }
	    }
	  }

	  
	  public void run() {
	    
	    String responseLine;
	    try {
	      while ((responseLine = data.readLine()) != null) {
	        System.out.println(responseLine);
	        if (responseLine.indexOf("*** Bye") != -1)
	          break;
	      }
	      closed = true;
	    } catch (IOException e) {
	      System.err.println("IOException:  " + e);
	    }

	}

}
