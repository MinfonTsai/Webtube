package com.test.webtube;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.IOException;  
import java.io.InputStream;
import java.net.Socket;  
import java.net.UnknownHostException;


public class ClientSocket {
	private String ip;
	private int port;
	private Socket socket = null;
	public DataOutputStream out = null;
	DataOutputStream getOutputStream = null;
	DataInputStream getMessageStream = null;
	public ClientSocket(String ip, int port) {
		this.ip = ip;
		this.port = port;
	} 
	public void createConnection() {
		try {
			socket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} finally {
		}
	}
	public void sendMessage(String sendMessage) {
		
		if( socket == null ) 
			return ;
			
		try {
			out = new DataOutputStream(socket.getOutputStream());
			if (sendMessage.equals("Windows")) {
				out.writeByte(0x1);
				out.flush();
				return;
			}
			if (sendMessage.equals("Android")) {
				out.writeByte(0x2);
				out.flush();
				return;
			}
			if (sendMessage.equals("Linux")) {
				out.writeByte(0x3);
				out.flush();
			} else {
				out.writeUTF(sendMessage);
				out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public DataOutputStream getOutputStream() {
		
		if( socket == null ) 
			return null;
			
		try {
			getOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				
			// return getMessageStream;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (getOutputStream != null) {
				try {
					getOutputStream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return getOutputStream;
	}
	public DataInputStream getMessageStream() {
		
		if( socket == null ) 
			return null;
			
		try {
			InputStream s1 = socket.getInputStream();
			if( s1 == null ) 
				return null;
			
		//	getMessageStream = new DataInputStream(new BufferedInputStream(
		//			socket.getInputStream()));
			// return getMessageStream;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (getMessageStream != null) {
				try {
					getMessageStream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		return getMessageStream;
	}
	public void shutDownConnection() {
		try {
			if (out != null) {
				out.close();
			}
			if (getMessageStream != null) {
				getMessageStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}