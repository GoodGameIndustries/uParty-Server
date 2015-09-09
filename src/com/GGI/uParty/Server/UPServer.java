/**
 * 
 */

/**
 * @author Emmett
 *
 */
package com.GGI.uParty.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.GGI.uParty.Network.Err;
import com.GGI.uParty.Network.Login;
import com.GGI.uParty.Network.Network;
import com.GGI.uParty.Network.Profile;
import com.GGI.uParty.Network.SignUp;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;


public class UPServer {

	
	private String htmlTemplate;
	private UI ui= new UI();
	private Server server;
	
	
	public UPServer(){
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader("uPartyEmail.html"));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
		}
		htmlTemplate = contentBuilder.toString();
		
		
		
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
		          //System.out.println("I received something");
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
		        		  Random ran = new Random();
	        			  String code= (100000 + ran.nextInt(900000)) + "";
	        			  p.verrCode=Integer.parseInt(code);
		        		  saveProfile(p);
		        		  
		        		  try {
		        			  
		        			  String msg = htmlTemplate.replace("$confirmation", code);
		        				new SendMailSSL().send(p.email,msg);
		        			} catch (Exception e1) {
		        				System.out.println("Unable to send email");
		        			}
		        	  }
		          }
		          
		          else if(object instanceof Login){
		        	  Login l = (Login)object;
		        	  String loc = l.email;
		        	  loc = loc.replace('.', '_');
		        	  loc = loc.replace('@', '_');
		        	  loc+=".profile";
		        	  File f = new File("D:\\profiles\\"+loc);
		        	  Profile p;
		        	  Err e = new Err();
		        	  e.message="Invalid email or password";
		        	 if(f.exists()){ p=loadProfile(l);
		        	 	if(l.pass.equals(p.pass)){connection.sendTCP(p);}
		        	 	else{connection.sendTCP(e);}
		        	 }
		        	 else{connection.sendTCP(e);}
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
	
	public Profile loadProfile(Login l){
		Profile result = null;
		 String loc = l.email;
	   	  loc = loc.replace('.', '_');
	   	  loc = loc.replace('@', '_');
	   	  loc+=".profile";
	   	  File f = new File("D:\\profiles\\"+loc);
		
	   	try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			result = (Profile) ois.readObject();
		} catch (Exception e) {
			System.out.println("load error");
			e.printStackTrace();
		}
		return result;
	}
	
	
}
