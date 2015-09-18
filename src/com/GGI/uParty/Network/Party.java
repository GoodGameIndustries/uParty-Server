package com.GGI.uParty.Network;

import java.util.ArrayList;
import java.util.Date;

public class Party {

	public int minAge = -1;
	public String name="0123456789012345678901234567890",where,description="01234567890123456789012345678901234567890123456789012345678901234567890123456789";
	public boolean open=true;
	public Date d = new Date();
	public int vote=0;
	public int id=-1;
	public Profile owner;
	public ArrayList<Profile> upVote = new ArrayList<Profile>();
	public ArrayList<Profile> downVote = new ArrayList<Profile>();
	public ArrayList<Profile> whitelist = new ArrayList<Profile>();
}
