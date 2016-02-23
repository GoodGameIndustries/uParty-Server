/**
 * 
 */
package com.GGI.uParty.Server;

import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Emmett
 *
 */
public class UI extends JFrame{

	private UPServer server;
	private JTextArea log;
	private JTextArea connected;
	private JTextField commandLine;
	private JButton enter;
	private JPanel sideP;
	private JPanel inputPanel;
	private ScrollPane scroll;
	private ScrollPane connectScroll;
	public UI(UPServer server){
		super("uParty Server");
		this.server=server;
		setSize(800,500);
		
		scroll = new ScrollPane();
		
		log = new JTextArea();
		log.setEditable(false);
		scroll.add(log);
		
		add(scroll,BorderLayout.CENTER);
		
		inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		
		commandLine = new JTextField();
		//commandLine.setSize(700, 25);
		
		inputPanel.add(commandLine,BorderLayout.CENTER);
		
		enter = new JButton("Enter");
		inputPanel.add(enter,BorderLayout.EAST);
		
		//add(inputPanel,BorderLayout.SOUTH);
		
		sideP = new JPanel();
		sideP.setLayout(new BorderLayout());
		
		connected = new JTextArea();
		connected.setEditable(false);
		connected.setSize(300, 500);
		
		connectScroll = new ScrollPane();
		connectScroll.setSize(300, 500);
		connectScroll.add(connected);
		sideP.add(connectScroll,BorderLayout.CENTER);
		
		add(sideP,BorderLayout.EAST);
		setVisible(true);
	}
	
	public void repaint(){
		log.setText(server.log);
		connected.setText(buildConnected());
		super.repaint();
	}

	private String buildConnected() {
		String result = "";
		Date d = new Date();
		for(int i = 0; i < server.connections.size(); i++){
			if(d.getTime()-server.connections.get(i).d.getTime()>120000){server.connections.remove(i);}
			else{
				result+="\n"+server.connections.get(i).p.name+"("+server.connections.get(i).p.email+")";
			}
		}
		result="There are "+server.connections.size()+" users connected: "+result;
		return result;
	}
	
}
