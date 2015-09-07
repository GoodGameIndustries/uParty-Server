/**
 * 
 */

/**
 * @author Emmett
 *
 */
package com.GGI.uParty.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.GGI.uParty.Network.Err;
import com.GGI.uParty.Network.Network;
import com.GGI.uParty.Network.Profile;
import com.GGI.uParty.Network.SignUp;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;

public class UPServer {

	private UI ui= new UI();
	private Server server;
	public UPServer(){
		server = new Server();
		server.start();
		System.out.println("Server starting...");
		try {
			server.bind(36693);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Network.register(server);
		server.addListener(new ThreadedListener(new Listener(){
			 public void received (Connection connection, Object object) {
		          System.out.println("I received something");
		          if(object instanceof SignUp){
		        	  SignUp o = (SignUp)object;
		        	  String loc = o.email;
		        	  loc = loc.replace('.', '_');
		        	  loc = loc.replace('@', '_');
		        	  loc+=".profile";
		        	  File f = new File("D:\\profiles\\"+loc);
		        	  if(f.exists()){
		        		  Err e = new Err();
		        		  e.message="This email is in use";
		        		  connection.sendTCP(e);
		        	  }
		        	  else{
		        		  Profile p = new Profile(o);
		        		  saveProfile(p);
		        	  }
		          }
		       }
			
		}));
	}
	
	public static void main(String[] args){
		new UPServer();
	}
	
	public void saveProfile(Profile p){
		 String loc = p.email;
   	  loc = loc.replace('.', '_');
   	  loc = loc.replace('@', '_');
   	  loc+=".profile";
   	  File f = new File("D:\\profiles\\"+loc);
   	  
   	  try {
   		f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(p);
	} catch (Exception e) {
		System.out.println("Save error");
		e.printStackTrace();
	}
	}
	
	
}
