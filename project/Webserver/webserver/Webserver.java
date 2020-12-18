package webserver;

import java.net.*;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;

public class Webserver extends Thread {
	protected Socket clientSocket;
	public static final String rootFolder = "/home/fenix/git/VVS/project/TestSite";
	private String fileType;
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(10008);
			System.out.println("Connection Socket Created");
			try {
				while (true) {
					System.out.println("Waiting for Connection");
					new Webserver(serverSocket.accept());
				}
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: 10008.");
			System.exit(1);
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Could not close port: 10008.");
				System.exit(1);
			}
		}
	}

	public Webserver(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}

	public void run() {
//		System.out.println("New Communication Thread Started");
		PrintWriter out=null;
//		PrintStream out = System.out;
		BufferedReader in=null;
		try {
			out = new PrintWriter(clientSocket.getOutputStream(),
					true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String inputLine;
			String commandLine;
			inputLine = in.readLine();
			commandLine = inputLine;
			do {
				if (inputLine.trim().equals(""))
					break;
				//out.println(inputLine);
				System.out.println("Server: " + inputLine);
			}while ((inputLine = in.readLine()) != null);
			if(commandLine==null) {
				System.out.println("Error ");
				System.exit(-1);
			}
			
			int indexSp=commandLine.indexOf(' ');
			if(indexSp == -1) {
				System.out.println("Format incorect");
				System.exit(-1);
			}
			
			String method = commandLine.substring(0, indexSp);
			commandLine = commandLine.substring(commandLine.indexOf(' ')+1);
			String requestedFile = commandLine.substring(0, commandLine.lastIndexOf("HTTP/")).trim();
			requestedFile = Webserver.rootFolder + requestedFile;
			
			if(requestedFile.endsWith("/")) { //vad daca e un folder sau nu
				requestedFile = requestedFile + "index.html"; // in index.html tin minte linkuri catre pagini
			}
			
			requestedFile = requestedFile.replaceAll("%20", " "); //inlocuiesc %20 cu spatiu pentru a fi corecte numele
			
			//aici iau tipul fisierului ce trebuie trimis
			switch(requestedFile.substring(requestedFile.lastIndexOf('.'))) {
			case ".html":
				fileType = "text/html; charset=UTF-8";
				break;
			case ".txt":
				fileType = "text/html; charset=UTF-8";
				break;
			case ".ico":
				fileType = "image/png";
				break;
			case ".jpg":
				fileType = "image/jpg";
				break;
			case ".css":
				fileType = "text/css; charset=UTF-8";
				break;
			}
			
			switch(method) { //aici determin ce functie a fost chemata - in cazul de fata doar head si get
			case "HEAD": 
				try {
					Pair fileContent = readFile(requestedFile);
					
					out.println("HTTP/1.1 200 OK");
					out.println("Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
					out.println("Server: Date: " + new Date());
					out.println("Server: Content-Type: " + fileType);
					out.println("Server: Content-Length: " + fileContent.length);
					out.println();
					}
					catch(FileNotFoundException e) {
						e.printStackTrace();
					}
					break;
			case "GET":
				try {
				Pair fileContent = readFile(requestedFile);
				
				out.println("HTTP/1.1 200 OK");
				out.println("Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
				out.println("Server: Date: " + new Date());
				out.println("Server: Content-Type: " + fileType);
				out.println("Server: Content-Length: " + fileContent.length);
				out.println();
				clientSocket.getOutputStream().write(fileContent.content);
				}
				catch(FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			default:
				System.out.println("Method unimplemented: " + method);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Problem with Communication Server");
			System.exit(1);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			System.out.println();
			if(out!=null)
				out.close();
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(clientSocket!=null)
				try {
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	private Pair readFile(String filePath) throws IOException {
		Pair retVal = new Pair();
		
		retVal.length = (int)Files.size(Paths.get(filePath));
		retVal.content = Files.readAllBytes(Paths.get(filePath));
		
		return retVal;
	}
	
}

class Pair{
	int length;
	byte[] content;
}