package com.GGI.uParty.Network;

public class Report extends Sendable{

	public Party p;
	public Profile rep;
	
	public Report(Party p,Profile rep) {
		this.p=p;
		this.rep=rep;
	}

	public Report(){}
	
	
}
