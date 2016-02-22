package com.GGI.uParty.Server;

import java.util.Date;

import com.GGI.uParty.Network.Profile;

public class Connected implements Comparable{

	public Profile p;
	public Date d;
	
	public Connected(Profile p, Date d){
		this.p=p;
		this.d=d;
		
	}
	
	@Override
	public int compareTo(Object o) {
		Connected c = (Connected)o;
		return d.compareTo(c.d);
	}

	
	
}
