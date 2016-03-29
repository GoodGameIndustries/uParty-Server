package com.GGI.uParty.Server.Data;

import java.util.ArrayList;
import java.util.Date;

import com.GGI.uParty.Server.UPServer;
import com.esotericsoftware.kryonet.Connection;

public class RefreshManager extends Thread{

	private UPServer s;
	private Date d = new Date();
	private int gap=50;
	
	
	
	public RefreshManager(UPServer s){
		this.s=s;
	}
	
	public void refresh(RefreshCheckpoint rF){
		System.out.println("refresh");
		s.refresh(rF);
		
		
	}

	@Override
	public void run() {
		
		
		while(true){
			try{
				d=new Date();
			Thread.sleep(1);
			if(s.readyToRefresh.size()>0){
				System.out.println(Math.abs(d.getTime()-s.readyToRefresh.get(0).d.getTime()));
			if(Math.abs(d.getTime()-s.readyToRefresh.get(0).d.getTime())>=gap){
				refresh(s.readyToRefresh.get(0));
				s.readyToRefresh.remove(0);
			}
			}
			}catch(Exception e){};
		}
		
	}
	
}
