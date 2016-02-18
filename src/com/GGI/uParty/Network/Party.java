package com.GGI.uParty.Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Party implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4763625482019105789L;
	public String name="",where="",description="";
	public Date d = new Date();
	public int vote=0;
	public String id=name+where+description;
	public Profile owner;
	public ArrayList<Profile> upVote = new ArrayList<Profile>();
	public ArrayList<Profile> downVote = new ArrayList<Profile>();

}
