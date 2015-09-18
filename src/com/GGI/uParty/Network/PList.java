package com.GGI.uParty.Network;

import java.io.Serializable;
import java.util.ArrayList;

public class PList implements Serializable {
	private static final long serialVersionUID = 1521736190810248707L;
	public ArrayList<Party> parties = new ArrayList<Party>();
	public String school ="";
	public PList(){}
	public PList(String school){this.school=school;}
}
