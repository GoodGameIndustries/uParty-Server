package com.GGI.uParty.Server.Data;

import java.util.ArrayList;
import java.util.TreeSet;

import com.GGI.uParty.Network.Party;

public class World {

	public TreeSet<DataPoint> points = new TreeSet<DataPoint>();
	public final double R = 6372.8; // Radius of earth In kilometers
	
	public World(){
		
	}
	
	public void add(float lon, float lat, Party p){
		DataPoint point = new DataPoint(lon,lat);
		if(points.contains(point)){
			points.ceiling(point).parties.add(p);
		}
		else{
		point.parties.add(p);
		points.add(point);
		}
		
	}
	
	public ArrayList<Party> getAround(float lon, float lat){
		int range = 50; //range in kilometers
		TreeSet<DataPoint> points = new TreeSet<DataPoint>();
		points.addAll(this.points);
		TreeSet<DataPoint> result = new TreeSet<DataPoint>();
		return null;
		
	}
	
	
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
