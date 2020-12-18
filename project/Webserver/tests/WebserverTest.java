package tests;


//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import webserver.Webserver;

public class WebserverTest {
	Socket mockSocket;
	Webserver obj;
	PipedOutputStream outMock, myOut;
	PipedInputStream inMock, myIn;
	BufferedReader in;
	PrintWriter out;
	String rootFolder = Webserver.rootFolder;
	
	//am folosit functia asta ca si o generalizare pentru fisierele html si txt
	//am preferat sa folosesc asta pentru html si txt pentru a nu mai da copy-paste, tinand cont ca html si txt sunt majoritatea testelor
	private void TestOut(String command, String filePath) throws IOException, InterruptedException {
		int fileLength = (int)Files.size(Paths.get(filePath));
		BufferedReader fileIn = new BufferedReader(new FileReader(new File(filePath)));
		String fileData=null, readData = null;
		
		out.println(command);
		out.close();
		
		
		String output = in.readLine();
		Assert.assertEquals(output,"HTTP/1.1 200 OK");
		output = in.readLine();
		Assert.assertEquals(output,"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
		//Assert.assertEquals(in.readLine(),"Date: " + new Date());//? trebuie revizuit
		in.readLine();
		output = in.readLine();
		Assert.assertEquals(output,"Server: Content-Type: text/html; charset=UTF-8");
		output = in.readLine();
		Assert.assertEquals(output,"Server: Content-Length: " + fileLength);
		output = in.readLine();
		Assert.assertEquals(output,"");
		while((fileData = fileIn.readLine())!=null) {
			readData = in.readLine();
			Assert.assertEquals(readData, fileData);
		}
		obj.join();
	}
	
	
	@Before
	public void init() {
//		mockSocket = mock(Socket.class);
		
		try {
			//pentru mocking am scris propria clasa, si am folosit pipeuri pentru comunicare intre program (care rula in thread) si test case-uri, 
			//am preferat varianta asta peste cea in care sa folosesc alte socketuri pentru comunicarea intre procese
			myOut = new PipedOutputStream();
			outMock = new PipedOutputStream();
			
			inMock = new PipedInputStream(myOut);
			myIn = new PipedInputStream(outMock);
			
			mockSocket = new SocketMock(outMock, inMock);
			
//			when(mockSocket.getOutputStream()).thenReturn(outMock);
//			when(mockSocket.getInputStream()).thenReturn(inMock);
			
			out = new PrintWriter(myOut,true);
			in = new BufferedReader(new InputStreamReader(myIn));
			
			obj=new Webserver(mockSocket);
		}
		catch(IOException e) {
			System.out.println("Exception occured");
			Assert.fail();
		}
	}
	
	@Test
	public void rootTest() {
		try {
			
			TestOut("GET / HTTP/1.1", rootFolder+"/index.html");
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	@Test
	public void aTest() {
		try {
			
			TestOut("GET /a.html HTTP/1.1", rootFolder+"/a.html");
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	@Test
	public void bTest() {
		try {
			
			TestOut("GET /b.html HTTP/1.1", rootFolder+"/b.html");
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	@Test
	public void a_bTest() {
		try {
			
			TestOut("GET /a%20b.html HTTP/1.1", rootFolder+"/a b.html");
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	@Test
	public void aaaDirTest() {
		try {
			
			TestOut("GET /aaa/ HTTP/1.1", rootFolder+"/aaa/index.html");
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	@Test
	public void yesImgTest() {  //pentru jpg nu am mai facut o functie care factorizeaza continutul, deoarece aveam deja o structura functionala
		try {
			int fileLength = (int)Files.size(Paths.get(rootFolder + "/yes.jpg"));
			BufferedReader fileIn = new BufferedReader(new FileReader(new File(rootFolder + "/yes.jpg")));
			int fileData, readData;
			
			out.println("GET /yes.jpg HTTP/1.1");
			out.close();
			
			
			String output = in.readLine();
			Assert.assertEquals(output,"HTTP/1.1 200 OK");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
			//Assert.assertEquals(in.readLine(),"Date: " + new Date());//? trebuie revizuit
			in.readLine();  //probleme cu Data, new Date() ia data curenta prea in detaliu, si nu are cum sa se potriveasca; trebuie sa fac o comparatie "partiala", fara secunde/milisecunde sau unde da eroare
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Type: image/jpg");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Length: " + fileLength);
			output = in.readLine();
			Assert.assertEquals(output,"");
			while((fileData = fileIn.read())!=-1) {
				readData = in.read();
				Assert.assertEquals(readData, fileData);
			}
			obj.join();
		}catch(Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void ye_sImgTest() {
		try {
			int fileLength = (int)Files.size(Paths.get(rootFolder + "/ye s.jpg"));
			BufferedReader fileIn = new BufferedReader(new FileReader(new File(rootFolder + "/ye s.jpg")));
			int fileData, readData;
			
			out.println("GET /ye%20s.jpg HTTP/1.1");
			out.close();
			
			
			String output = in.readLine();
			Assert.assertEquals(output,"HTTP/1.1 200 OK");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
			//Assert.assertEquals(in.readLine(),"Date: " + new Date());//? trebuie revizuit
			in.readLine();  //probleme cu Data, new Date() ia data curenta prea in detaliu, si nu are cum sa se potriveasca; trebuie sa fac o comparatie "partiala", fara secunde/milisecunde sau unde da eroare
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Type: image/jpg");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Length: " + fileLength);
			output = in.readLine();
			Assert.assertEquals(output,"");
			while((fileData = fileIn.read())!=-1) {
				readData = in.read();
				Assert.assertEquals(readData, fileData);
			}
			obj.join();
		}catch(Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void aaaDir_bTest() {
try {
			
			TestOut("GET /aaa/b.html HTTP/1.1", rootFolder+"/aaa/b.html");
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	@Test
	public void aaaDir_bbbDir_yesImgTest() {
		try {
			int fileLength = (int)Files.size(Paths.get(rootFolder + "/aaa/bbb/yes.jpg"));
			BufferedReader fileIn = new BufferedReader(new FileReader(new File(rootFolder + "/aaa/bbb/yes.jpg")));
			int fileData, readData;
			
			out.println("GET /aaa/bbb/yes.jpg HTTP/1.1");
			out.close();
			
			
			String output = in.readLine();
			Assert.assertEquals(output,"HTTP/1.1 200 OK");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
			//Assert.assertEquals(in.readLine(),"Date: " + new Date());//? trebuie revizuit
			in.readLine();  //probleme cu Data, new Date() ia data curenta prea in detaliu, si nu are cum sa se potriveasca; trebuie sa fac o comparatie "partiala", fara secunde/milisecunde sau unde da eroare
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Type: image/jpg");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Length: " + fileLength);
			output = in.readLine();
			Assert.assertEquals(output,"");
			while((fileData = fileIn.read())!=-1) {
				readData = in.read();
				Assert.assertEquals(readData, fileData);
			}
			obj.join();
		}catch(Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void txtTest() {
try {
			
			TestOut("GET /a.txt HTTP/1.1", rootFolder+"/a.txt");
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	@Test
	public void cssTest() { //css test - nu am adaugat mai mult de un fisier css in cadrul testelor (fisierelor ce pot fi accesate prin intermediul serverului)
		try {
			
			int fileLength = (int)Files.size(Paths.get(rootFolder+"/style.css"));
			BufferedReader fileIn = new BufferedReader(new FileReader(new File(rootFolder+"/style.css")));
			String fileData=null, readData = null;
			
			out.println("GET /style.css HTTP/1.1");
			out.close();
			
			
			String output = in.readLine();
			Assert.assertEquals(output,"HTTP/1.1 200 OK");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
			//Assert.assertEquals(in.readLine(),"Date: " + new Date());//? trebuie revizuit
			in.readLine();
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Type: text/css; charset=UTF-8");
			output = in.readLine();
			Assert.assertEquals(output,"Server: Content-Length: " + fileLength);
			output = in.readLine();
			Assert.assertEquals(output,"");
			while((fileData = fileIn.readLine())!=null) {
				readData = in.readLine();
				Assert.assertEquals(readData, fileData);
			}
			obj.join();
			
			
		}
		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
	}
	
	
	
	
	
}


//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// Incercari vechi si esuate -- am preferat sa le las in program, doar de idee
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
//	@Test
//	public void aTest() {
//		try {
//			
//			TestOut(rootFolder+"/a.html");
//			
//		}
//		catch (IOException e){e.printStackTrace(); System.out.println("IOException occured");} 
//		catch (InterruptedException e) {e.printStackTrace(); System.out.println("IException occured");}
//	}
//	
//	@Test
//	public void bTest() {
//		try {
//			int fileLength=0;
//			byte[] fileData, readData;
//			File file = new File("TestSite/b.html");
//			
//			fileLength = (int) file.length();
//			fileData = new byte[fileLength+1];
//			readData = new byte[fileLength+1];
//			FileInputStream fileIn = new FileInputStream(file);
//			fileIn.read(fileData);
//			fileIn.close();
//			
//			out.println("GET /b HTTP/1.1");
//			
//			BufferedInputStream bis = new BufferedInputStream(myIn);
//			
//			Assert.assertEquals(in.readLine(),"HTTP/1.1 200 OK");
//			Assert.assertEquals(in.readLine(),"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
//			Assert.assertEquals(in.readLine(),"Date: " + new Date());//? trebuie revizuit
//			Assert.assertEquals(in.readLine(),"Content-Type: text/html; charset=UTF-8");
//			Assert.assertEquals(in.readLine(),"Content-Length: " + fileLength);
//			bis.read(readData);
//			Assert.assertArrayEquals(fileData, readData);
//			
//			obj.join();
//		}
//		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
//		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
//	}
//	
//	@Test
//	public void aaaDirTest() {
//		try {
//			TestOut(rootFolder+"/aaa/index.html");
////			int fileLength=0;
////			byte[] fileData, readData;
////			File file = new File("TestSite/aaa/index.html");
////			
////			fileLength = (int) file.length();
////			fileData = new byte[fileLength+1];
////			readData = new byte[fileLength+1];
////			FileInputStream fileIn = new FileInputStream(file);
////			fileIn.read(fileData);
////			fileIn.close();
////			
////			out.println("GET /aaa/ HTTP/1.1");
////			
////			BufferedInputStream bis = new BufferedInputStream(myIn);
////			
////			Assert.assertEquals(in.readLine(),"HTTP/1.1 200 OK");
////			Assert.assertEquals(in.readLine(),"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
////			Assert.assertEquals(in.readLine(),"Date: " + new Date());//? trebuie revizuit
////			Assert.assertEquals(in.readLine(),"Content-Type: text/html; charset=UTF-8");
////			Assert.assertEquals(in.readLine(),"Content-Length: " + fileLength);
////			bis.read(readData);
////			Assert.assertArrayEquals(fileData, readData);
////			
////			obj.join();
//		}
//		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
//		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
//	}
//	
//	@Test
//	public void aaaDir_bTest() {
//		try {
//			
//			TestOut(rootFolder+"/aaa/b.html");
////			int fileLength=0;
////			byte[] fileData, readData;
////			File file = new File("TestSite/aaa/b.html");
////			
////			fileLength = (int) file.length();
////			fileData = new byte[fileLength+1];
////			readData = new byte[fileLength+1];
////			FileInputStream fileIn = new FileInputStream(file);
////			fileIn.read(fileData);
////			fileIn.close();
////			
////			out.println("GET /aaa/b.html HTTP/1.1");
////			
////			BufferedInputStream bis = new BufferedInputStream(myIn);
////			
////			Assert.assertEquals(in.readLine(),"HTTP/1.1 200 OK");
////			Assert.assertEquals(in.readLine(),"Server: Java HTTP Server upt.ac.SSC.AlexPescaru : 1.0");
////			Assert.assertEquals(in.readLine(),"Date: " + new Date()); //? trebuie revizuit
////			Assert.assertEquals(in.readLine(),"Content-Type: text/html; charset=UTF-8");
////			Assert.assertEquals(in.readLine(),"Content-Length: " + fileLength);
////			bis.read(readData);
////			Assert.assertArrayEquals(fileData, readData);
////			
////			obj.join();
//		}
//		catch (IOException e){System.out.println("IOException occured"); e.printStackTrace(); Assert.fail();} 
//		catch (InterruptedException e) {System.out.println("IException occured"); e.printStackTrace(); Assert.fail();}
//	}
	
//	@Test
//	public void emptyTest() {
//		
//		try {
//			out.println("");
//			String out = in.readLine();
//			Assert.assertEquals(out, "");
//			
//			obj.join();
//		}
//		catch(IOException e){System.out.println("IOException occured");} 
//		catch (InterruptedException e) {System.out.println("IException occured");}	
//	}
//	
//	@Test
//	public void oneTest() {
//		try {
//			out.println("test");
//			Assert.assertEquals(in.readLine(), "test");
//			out.println("");
//			Assert.assertEquals(in.readLine(), "");
//			
//			obj.join();
//		}
//		catch(IOException e){} 
//		catch (InterruptedException e) {}	
//	}
	
//}
