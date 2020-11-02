package tests;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

public class SocketMock extends Socket {
	private PipedOutputStream outMock;
	private PipedInputStream inMock;
	
	public SocketMock(PipedOutputStream outMock, PipedInputStream inMock) {
		this.inMock = inMock;
		this.outMock = outMock;
	}
	
	@Override
	public OutputStream getOutputStream() {
		return outMock;
	}
	
	@Override
	public	InputStream getInputStream() {
		return inMock;
	}
	
	@Override
	public void close() {
		
	}
}
