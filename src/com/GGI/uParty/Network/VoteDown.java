package com.GGI.uParty.Network;

public class VoteDown extends Sendable{
	public Party p;
	public Profile voter;
	
	public VoteDown(){}
	public VoteDown(Party p , Profile voter){
		this.p=p;
		this.voter=voter;
	}
}
