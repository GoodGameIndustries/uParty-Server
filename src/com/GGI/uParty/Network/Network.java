package com.GGI.uParty.Network;

import java.util.ArrayList;
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
		kryo.register(Verify.class);
		kryo.register(ResendConfirmation.class);
		kryo.register(PList.class);
		kryo.register(Refresh.class);
		kryo.register(ArrayList.class);
		kryo.register(CreateParty.class);
		kryo.register(Party.class);
		kryo.register(VoteUp.class);
		kryo.register(VoteDown.class);
		kryo.register(Forgot.class);
	}
	
}
