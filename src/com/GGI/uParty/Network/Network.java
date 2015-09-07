package com.GGI.uParty.Network;

import java.util.Date;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Sendable.class);
		kryo.register(SignUp.class);
		kryo.register(Date.class);
		kryo.register(Err.class);
		kryo.register(Profile.class);
		kryo.register(Login.class);
	}
	
}
