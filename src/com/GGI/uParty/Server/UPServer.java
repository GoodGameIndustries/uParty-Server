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
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import com.GGI.uParty.Network.Forgot;
import com.GGI.uParty.Network.Login;
import com.GGI.uParty.Network.Network;
import com.GGI.uParty.Network.PList;
import com.GGI.uParty.Network.Profile;
import com.GGI.uParty.Network.Refresh;
import com.GGI.uParty.Network.ResendConfirmation;
import com.GGI.uParty.Network.SignUp;
import com.GGI.uParty.Network.Verify;
import com.GGI.uParty.Network.VoteDown;
import com.GGI.uParty.Network.VoteUp;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;


public class UPServer {

	
	private String htmlTemplate;
	private String forgotTemplate;
	private UI ui= new UI();
	private Server server;
	private boolean debug = false;
	private String path = debug?"D:\\profiles\\":"C:\\Users\\Administrator\\Google Drive\\uParty\\profiles\\";
	private Timer timer;
	public String version = "1.0.2";
	
	
	public UPServer(){
		
		timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                System.exit(0);
            }
        }, 21600000);
		
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
		
		contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader("uPartyForgotEmail.html"));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
		}
		forgotTemplate = contentBuilder.toString();
		
		
		
		server = new Server();
		server.start();
		System.out.println("Server starting...");
		System.out.println("\tStart Time = "+new Date().toString());
		System.out.println("\tDebug Mode = " + debug);
		
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
		        	 
		        	  
		        	  if(l.version.equals(version)){
		        	  Profile p=null;
		        	  Err e = new Err();
		        	  e.message="Invalid email or password";
		        	  	p=loadProfile(l.email);
		        	 	if(p!=null&&l.pass.equals(p.pass)){connection.sendTCP(p);}
		        	 	else{connection.sendTCP(e);}
		        	  }
		        	  else{
		        		  Err e = new Err();
		        		  e.message="Version";
		        		  connection.sendTCP(e);
		        	  }
		          }
		          
		          else if(object instanceof Verify){
		        	  Verify v = (Verify)object;
		        	  Profile p = loadProfile(v.email);
		        	  p.verr=true;
		        	  saveProfile(p);
		        	  
		          }
		          
		          else if(object instanceof Refresh){
		        	  Refresh r = (Refresh)object;
		        	  try{
		        	  PList p =loadPList(r.p.email.split("@")[1]);
		
						for(int i = 0; i < p.parties.size();i++){
							connection.sendTCP(p.parties.get(i));
						}
						
		        	  }
		        	  catch(Exception e){System.out.println("Send Error");}
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
		        	  cp.p.id=cp.p.name+cp.p.where+cp.p.description;
		        	  pL.parties.add(cp.p);
		        	  savePList(pL);
		        	  
		        	  
		          }
		          else if(object instanceof VoteUp){
		        	  VoteUp v = (VoteUp)object;
		        	  PList pL = loadPList(v.voter.email.split("@")[1]);
		        	  for(int i = 0;i<pL.parties.size();i++){if(v.p.id.equals(pL.parties.get(i).id)){pL.parties.remove(i);}}
		        	  if(!v.p.upVote.contains(v.voter)){v.p.upVote.add(v.voter);}
		        	  if(v.p.downVote.contains(v.voter)){v.p.downVote.remove(v.voter);}
		        	  v.p.vote=v.p.upVote.size()-v.p.downVote.size();
		        	  pL.parties.add(v.p);
		        	  savePList(pL);
		        	  //connection.sendTCP(pL);
		        	  
		          }
		          else if(object instanceof VoteDown){
		        	  VoteDown v = (VoteDown)object;
		        	  PList pL = loadPList(v.voter.email.split("@")[1]);
		        	  for(int i = 0;i<pL.parties.size();i++){if(v.p.id.equals(pL.parties.get(i).id)){pL.parties.remove(i);}}
		        	  if(!v.p.downVote.contains(v.voter)){v.p.downVote.add(v.voter);}
		        	  if(v.p.upVote.contains(v.voter)){v.p.upVote.remove(v.voter);}
		        	  v.p.vote=v.p.upVote.size()-v.p.downVote.size();
		        	  pL.parties.add(v.p);
		        	  savePList(pL);
		        	  //connection.sendTCP(pL);
		        	  
		          }
		          else if(object instanceof Forgot){
		        	  Forgot f = (Forgot)object;
		        	  Profile p = loadProfile(f.e);
		        	  
		        	  try {
	        			  
	        			  String msg = forgotTemplate.replace("$password", p.pass);
	        				new SendMailSSL().send(p.email,msg);
	        			} catch (Exception e1) {
	        				System.out.println("Unable to send email");
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
			PList p =new PList(school);
			savePList(p);
			result=p;
		}
		result = clearOldParties(result);
		savePList(result);
		return result;
	}

	private PList clearOldParties(PList p) {
		Date d = new Date();
		for(int i = 0; i < p.parties.size(); i++){
			if(Math.abs(d.getDate()-p.parties.get(i).d.getDate())>1){
				p.parties.remove(i);
			}
		}
		return p;
	}
	
}
