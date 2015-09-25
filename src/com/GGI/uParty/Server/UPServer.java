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

import com.GGI.uParty.Network.CreateParty;
import com.GGI.uParty.Network.Err;
import com.GGI.uParty.Network.Login;
import com.GGI.uParty.Network.Network;
import com.GGI.uParty.Network.PList;
import com.GGI.uParty.Network.Profile;
import com.GGI.uParty.Network.Refresh;
import com.GGI.uParty.Network.ResendConfirmation;
import com.GGI.uParty.Network.SignUp;
import com.GGI.uParty.Network.Verify;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;


public class UPServer {

	
	private String htmlTemplate;
	private UI ui= new UI();
	private Server server;
	private String path = "C:\\Users\\Administrator\\Google Drive\\uParty\\profiles\\";
	
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
		        	  File f = new File(path+loc);
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
		        		  connection.sendTCP(p);
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
		        	 
		        	  Profile p=null;
		        	  Err e = new Err();
		        	  e.message="Invalid email or password";
		        	  	p=loadProfile(l.email);
		        	 	if(p!=null&&l.pass.equals(p.pass)){connection.sendTCP(p);}
		        	 	else{connection.sendTCP(e);}
		          }
		          
		          else if(object instanceof Verify){
		        	  Verify v = (Verify)object;
		        	  Profile p = loadProfile(v.email);
		        	  p.verr=true;
		        	  saveProfile(p);
		        	  
		          }
		          
		          else if(object instanceof Refresh){
		        	  Refresh r = (Refresh)object;
		        	  connection.sendTCP(loadPList(r.p.email.split("@")[1]));
		          }
		          
		          else if(object instanceof ResendConfirmation){
		        	  ResendConfirmation r = (ResendConfirmation)object;
		        	  Profile p = loadProfile(r.email);
		        	  Random ran = new Random();
        			  String code= (100000 + ran.nextInt(900000)) + "";
        			  p.verrCode=Integer.parseInt(code);
	        		  saveProfile(p);
	        		  connection.sendTCP(p);
	        		  try {
	        			  
	        			  String msg = htmlTemplate.replace("$confirmation", code);
	        				new SendMailSSL().send(p.email,msg);
	        			} catch (Exception e1) {
	        				System.out.println("Unable to send email");
	        			}
		        	  
		        	  
		          }
		          else if(object instanceof CreateParty){
		        	  CreateParty cp = (CreateParty)object;
		        	  PList pL = loadPList(cp.p.owner.email.split("@")[1]);
		        	  pL.parties.add(cp.p);
		        	  savePList(pL);
		        	  
		          }
		       }
			
		}));
	}
	
	public static void main(String[] args){
		new UPServer();
	}
	
	public void saveProfile(Profile p){
		 String loc = p.email;
		 String dir="";
	   	  loc = loc.replace('.', '_');
	   	  dir = loc.split("@")[1];
	   	  loc = loc.replace('@', '_');
   	  loc+=".profile";
   	  File directory = new File(path+dir);
   	  if(!directory.exists()){directory.mkdir();savePList(new PList(dir));}
   	  File f = new File(path+dir+"\\"+loc);
   	  
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
	
	public Profile loadProfile(String l){
		
		Profile result = null;
		try {
		String loc = l;
		 String dir="";
	   	  loc = loc.replace('.', '_');
	   	  dir = loc.split("@")[1];
	   	  loc = loc.replace('@', '_');
	   	  loc+=".profile";
	   	  File f = new File(path+dir+"\\"+loc);
	   	  
		
	   	
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			result = (Profile) ois.readObject();
		} catch (Exception e) {
			System.out.println("load error");
			//e.printStackTrace();
		}
		return result;
	}
	
	public void savePList(PList p){
		try{
			File f = new File(path+p.school+"\\"+"parties.pList");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(p);
		}catch(Exception e){
			System.out.println("party save error");
		}
	}
	public PList loadPList(String school){
		PList result = null;
		school = school.replace('.', '_');
		try{
			File f = new File(path+school+"\\"+"parties.pList");
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			result = (PList) ois.readObject();
		}catch(Exception e){
			System.out.println("party load error");
		}
		return result;
	}
	
}
