		/**
		 * 
		 */
		package MultiPersonChatApplication;
		import java.io.DataInputStream;
		import java.io.PrintStream;
		import java.io.IOException;
		import java.net.Socket;
		import java.net.ServerSocket;
		/**
		 * @author NoteBook
		 *
		 */
		public class MultiThreadChatServerSync {
		
			  private static ServerSocket serverSocket = null;
			  
			  private static Socket clientSocket = null;
		
			 
			  private static final int maxClientsCount = 11;
			  private static final clientThread[] threads = new clientThread[maxClientsCount];
		
			public static void main(String[] args) {
				
			    int portNumber = 40000;
			    if (args.length < 2) {
			      System.out.println(" java MultiThreadChatServerSync <portNumber>\n"
			          + "Now using port number=" + portNumber);
			    }
			    else {
			      portNumber = Integer.valueOf(args[0]).intValue();
			    }
		
			    
			    try {
			      serverSocket = new ServerSocket(portNumber);
			    } catch (IOException e) {
			      System.out.println(e);
			    }
		
			    
			    while (true) {
			      try {
			    	  
			        clientSocket = serverSocket.accept();
			        int i = 0;
			        for (i = 0; i < maxClientsCount; i++) {
			          if (threads[i] == null) {
			            (threads[i] = new clientThread(clientSocket, threads)).start();
			            break;
			          }
			        }
			        if (i == maxClientsCount) {
			          PrintStream os = new PrintStream(clientSocket.getOutputStream());
			          os.println("Server too busy. Try later.");
			          os.close();
			          clientSocket.close();
			        }
			      } catch (IOException e) {
			        System.out.println(e);
			      }
			    }
			  }
			}
		
			
			class clientThread extends Thread {
		
			  private String clientName = null;
			  private DataInputStream data = null;
			  private PrintStream output = null;
			  private Socket clientSocket = null;
			  private final clientThread[] threads;
			  private int maxClientsCount;
		
			  public clientThread(Socket clientSocket, clientThread[] threads) {
			    this.clientSocket = clientSocket;
			    this.threads = threads;
			    maxClientsCount = threads.length;
			  }
		
			  public void run() {
			    int maxClientsCount = this.maxClientsCount;
			    clientThread[] threads = this.threads;
		
			    try {
			      
			      data = new DataInputStream(clientSocket.getInputStream());
			      output = new PrintStream(clientSocket.getOutputStream());
			      String name;
			      while (true) {
			        output.println("Enter your name.");
			        name = data.readLine().trim();
			        if (name.indexOf('@') == -1) {
			          break;
			        } else {
			          output.println("The name should not contain '@' character.");
			        }
			      }
		
			     
			      output.println("Welcome " + name
			          + " to our euclidian chat room.\nTo leave enter /quit in a new line.");
			      synchronized (this) {
			        for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] != null && threads[i] == this) {
			            clientName = "@" + name;
			            break;
			          }
			        }
			        for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] != null && threads[i] != this) {
			            threads[i].output.println("*** A new user " + name
			                + " entered the chat room !!! ***");
			          }
			        }
			      }
			     
			      while (true) {
			        String line = data.readLine();
			        if (line.startsWith("quit")) {
			          break;
			        }
			       
			        if (line.startsWith("@")) {
			          String[] words = line.split("\\s", 2);
			          if (words.length > 1 && words[1] != null) {
			            words[1] = words[1].trim();
			            if (!words[1].isEmpty()) {
			              synchronized (this) {
			                for (int i = 0; i < maxClientsCount; i++) {
			                  if (threads[i] != null && threads[i] != this
			                      && threads[i].clientName != null
			                      && threads[i].clientName.equals(words[0])) {
			                    threads[i].output.println("<" + name+" "+ threads[i] + "> " + words[1]);
			                   
			                    this.output.println(">" + name + "> " + words[1]);
			                    break;
			                  }
			                }
			              }
			            }
			          }
			        } 
			        else {
			          
			          synchronized (this) {
			            for (int i = 0; i < maxClientsCount; i++) {
			              if (threads[i] != null && threads[i].clientName != null) {
			                threads[i].output.println("<" + name + "> " + line);
			              }
			            }
			          }
			        }
			      }
			      synchronized (this) {
			        for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] != null && threads[i] != this
			              && threads[i].clientName != null) {
			            threads[i].output.println("*** The user " + name+" "+ threads[i]
			                + " is leaving the chat room !!! ***");
			          }
			        }
			      }
			      output.println("*** Bye " + name + " ***");
		
			      synchronized (this) {
			        for (int i = 0; i < maxClientsCount; i++) {
			          if (threads[i] == this) {
			            threads[i] = null;
			          }
			        }
			      }
			      
			      data.close();
			      output.close();
			      clientSocket.close();
			    } catch (IOException e) {
			    }
			}
		
		}
