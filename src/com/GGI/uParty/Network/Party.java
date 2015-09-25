package com.GGI.uParty.Network;

import java.util.ArrayList;
import java.util.Date;

public class Party {

	public String name="",where="",description="";
	public Date d = new Date();
	public int vote=0;
	public Profile owner;
	public ArrayList<Profile> upVote = new ArrayList<Profile>();
	public ArrayList<Profile> downVote = new ArrayList<Profile>();
}
