package com.GGI.uParty.Network;

public class VoteUp extends Sendable{
	public Party p;
	public Profile voter;
	
	public VoteUp(){}
	public VoteUp(Party p, Profile voter){
		this.p=p;
		this.voter=voter;
	}
}
