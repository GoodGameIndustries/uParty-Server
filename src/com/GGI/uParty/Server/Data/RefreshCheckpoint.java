package com.GGI.uParty.Server.Data;

import java.util.Date;

import com.esotericsoftware.kryonet.Connection;

public class RefreshCheckpoint {
	
	public Connection connection;
	public Date d;
	public String email;

	public RefreshCheckpoint(Connection connection, Date d, String email){
		this.connection=connection;
		this.d=d;
		this.email=email;
		
		System.out.println("Refresh Created");
	}
	
}
