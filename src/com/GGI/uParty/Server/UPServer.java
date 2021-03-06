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
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.GGI.uParty.Network.CreateParty;
import com.GGI.uParty.Network.Err;
import com.GGI.uParty.Network.Forgot;
import com.GGI.uParty.Network.Login;
import com.GGI.uParty.Network.Network;
import com.GGI.uParty.Network.PList;
import com.GGI.uParty.Network.Profile;
import com.GGI.uParty.Network.Refresh;
import com.GGI.uParty.Network.Report;
import com.GGI.uParty.Network.ResendConfirmation;
import com.GGI.uParty.Network.SignUp;
import com.GGI.uParty.Network.Verify;
import com.GGI.uParty.Network.VoteDown;
import com.GGI.uParty.Network.VoteUp;
import com.GGI.uParty.Server.Data.RefreshCheckpoint;
import com.GGI.uParty.Server.Data.RefreshManager;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;


public class UPServer {

	
	private String htmlTemplate;
	private String forgotTemplate;
	private UI ui= new UI(this);
	private Server server;
	private boolean debug = true;
	private String path = debug?"D:\\profiles\\":"C:\\Users\\Administrator\\Google Drive\\uParty\\profiles\\";
	private Timer timer;
	public String version = "1.0.8";
	//public String maxL="                                                                                                                                                                                         ";
	public String bleep="**********************************************************************";
	public String[] badWords;
	public String log="";
	public ArrayList<Connected> connections = new ArrayList<Connected>();
	public RefreshManager rM;
	public ArrayList<RefreshCheckpoint>  readyToRefresh = new ArrayList<RefreshCheckpoint>();
	
	public UPServer(){
		rM=new RefreshManager(this);
		Thread t = rM;
		t.start();
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
		
		contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader("badWords.txt"));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(":"+str);
		    }
		    in.close();
		} catch (IOException e) {
		}
		badWords = contentBuilder.toString().split(":");
		
		server = new Server();
		server.start();
		printConsole("Server starting...");
		printConsole("\tStart Time = "+new Date().toString());
		printConsole("\tDebug Mode = " + debug);
		printConsole("\tVersion = "+version);
		printConsole("\tBad Words Loaded = "+badWords.length);
		
		try {
			server.bind(36693);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Network.register(server);
		server.addListener(new ThreadedListener(new Listener(){
			 public void received (Connection connection, Object object) {
		          //printConsole("I received something");
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
		        				printConsole("Unable to send email");
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
		        	  boolean add = false;
		        	  
		        	  for(int i = 0; i < connections.size();i++){
		        		  if(connections.get(i).p.email.equals(r.p.email)){
		        			  connections.remove(i);
		        			  connections.add(0,new Connected(r.p,new Date()));
		        			  add=true;
		        			  break;
		        		  }
		        	  }
		        	  if(!add){
		        		  connections.add(0,new Connected(r.p,new Date()));
		        	  }
		        	  RefreshCheckpoint cP = new RefreshCheckpoint(connection,new Date(),r.p.email);
		        	  if(!readyToRefresh.contains(cP)){
		        	  readyToRefresh.add(cP);
		        	  }
		        	  ui.repaint();
		        	  
		        	  
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
	        				printConsole("Unable to send email");
	        			}
		        	  
		        	  
		          }
		          else if(object instanceof CreateParty){
		        	  CreateParty cp = (CreateParty)object;
		        	  String[] split = cp.p.owner.email.split("@")[1].split("\\.");
		        	  PList pL = loadPList(split[split.length-2]+"_"+split[split.length-1]);
		        	  cp.p.where=badWords(cp.p.where.toLowerCase());
		        	  cp.p.where=cp.p.where;
		        	 
		        	  
		        	  
		        	  
		        	  cp.p.description=badWords(cp.p.description.toLowerCase());
		        	  cp.p.description=cp.p.description;
		        	  
		        	  
		        	  cp.p.name=badWords(cp.p.name.toLowerCase());
		        	  
		        	  cp.p.id=cp.p.name+cp.p.where+cp.p.description;
		        	  pL.parties.add(cp.p);
		        	  savePList(pL);
		        	  
		        	  
		          }
		          else if(object instanceof VoteUp){
		        	  VoteUp v = (VoteUp)object;
		        	  String[] split = v.p.owner.email.split("@")[1].split("\\.");
		        	  PList pL = loadPList(split[split.length-2]+"_"+split[split.length-1]);
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
		        	  String[] split = v.p.owner.email.split("@")[1].split("\\.");
		        	  PList pL = loadPList(split[split.length-2]+"_"+split[split.length-1]);
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
	        				printConsole("Unable to send email");
	        			}
		          }
		          
		          else if(object instanceof Report){
		        	  Report r = (Report) object;
		        	  
		        	  String msg = "Reporter: "+r.rep.email+"\nParty-----\nName: "+r.p.name+"\nDescription: "+r.p.description+
		        			  "\nLocation: "+r.p.where+"\nOwner: "+r.p.owner.email;
		        	  try {
	        				new SendMailSSL().send("goodgameindustries@gmail.com",msg);
	        			} catch (Exception e1) {
	        				printConsole("Unable to send email");
	        			}
		          }
		       }

			private String badWords(String where) {
				String result = "";
				//printConsole(result);
				/**
				for(int i = 0; i < badWords.length; i++){
					if(badWords[i]!=null&&badWords[i].length()>0){
					result=result.replaceAll(badWords[i], bleep.substring(0,badWords[i].length()));
					}
					}
					*/
				
				String[] breakDown = where.split(" ");
				for(int i = 0; i < breakDown.length; i++){
					for(int j = 0; j < badWords.length; j++){
						if(breakDown[i].equals(badWords[j])){
							breakDown[i]=bleep.substring(0,breakDown[i].length());
						}
					}
				}
				
				for(int i = 0; i < breakDown.length;i++){
					if(i>0){result+=" ";}
					result+=breakDown[i];
				}
				//printConsole(result);
				return result;
			}
			
		}));
	}
	
	private void printConsole(String string) {
		Date d = new Date();
		log+="\n"+"("+d.getHours()+":"+d.getMinutes()+")"+" UPServer:> "+string;
		ui.repaint();
		
	}

	public static void main(String[] args){
		new UPServer();
	}
	
	public void saveProfile(Profile p){
		 String loc = p.email;
		 String dir="";
		 System.out.println(p.email);
	   	 String[] split = loc.split("@")[1].split("\\.");
	   	 System.out.println(split.length);
	   	  dir = split[split.length-2]+"_"+split[split.length-1];
	   	  loc = loc.replace('.', '_');
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
		printConsole("Save error");
		e.printStackTrace();
	}
	}
	
	public Profile loadProfile(String l){
		
		Profile result = null;
		try {
		String loc = l;
		 String dir="";
	   	  
	   	 String[] split = loc.split("@")[1].split("\\.");
	   	 dir = split[split.length-2]+"_"+split[split.length-1];
	   	  loc = loc.replace('.', '_');
	   	  loc = loc.replace('@', '_');
	   	  loc+=".profile";
	   	  File f = new File(path+dir+"\\"+loc);
	   	  
		
	   	
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			result = (Profile) ois.readObject();
		} catch (Exception e) {
			printConsole("load error");
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
			printConsole("party save error");
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
			printConsole("party load error");
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
			
			if(d.getTime()-p.parties.get(i).endD.getTime()>0){
				p.parties.remove(i);
			}
			
		}
		return p;
	}
	
	public void refresh(RefreshCheckpoint r){
		System.out.println("refreshing");
		try{
			
			String[] split = r.email.split("@")[1].split("\\.");
      	  PList p = loadPList(split[split.length-2]+"_"+split[split.length-1]);

				for(int i = 0; i < p.parties.size();i++){
					if(p.parties.get(i).upVote.size()-p.parties.get(i).downVote.size()>-5){
						if(p.parties.get(i).description.length()>=105&&p.parties.get(i).where.length()>=105){
						r.connection.sendTCP(p.parties.get(i));
						}
						else{
							//p.parties.get(i).description=(p.parties.get(i).description+maxL).substring(0, 105);
							//p.parties.get(i).where=(p.parties.get(i).where+maxL).substring(0, 105);
							r.connection.sendTCP(p.parties.get(i));
						}
					
					
					
					}
				}
				
      	  }
      	  catch(Exception e){printConsole("Send Error");}
	}
	
}
