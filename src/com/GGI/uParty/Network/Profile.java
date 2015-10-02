package com.GGI.uParty.Network;

import java.io.Serializable;
import java.util.Date;

public class Profile implements Serializable{
	private static final long serialVersionUID = 6031323640522278194L;
	public Date d = new Date();
	public String name = "",email = "",pass = "",bio = "";
	public boolean verr = false;
	public int verrCode = 0;
	
	public Profile(SignUp s){
		this.d=s.date;
		this.name=s.name;
		this.email=s.email;
		this.pass=s.pass;
	}
	
	public Profile(){
		
	}
	
	public boolean equals(Object o){
		if(o instanceof Profile){
			Profile p = (Profile) o;
			return (name.equals(p.name)&&email.equals(p.email)&&pass.equals(p.pass));
		}
		return false;
	}
}
