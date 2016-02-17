package com.GGI.uParty.Server.Data;

import java.util.ArrayList;

import com.GGI.uParty.Network.Party;

public class DataPoint implements Comparable{

	public ArrayList<Party> parties = new ArrayList<Party>();
	private float x,y;
	
	public DataPoint(float x, float y){
		this.x=x;this.y=y;
	}
	
	@Override
	public int compareTo(Object e) {
		DataPoint p = (DataPoint)e;
		int result = 0;
		if (p.x==this.x){
			if(p.y>this.y){
				result=-1;
			}
			else if(p.y<this.y){
				result=1;
			}
		}
		else if(p.x>this.x){
			result=-1;
		}
		else if(p.x<this.x){
			result=1;
		}
		return result;
	}
	
}



