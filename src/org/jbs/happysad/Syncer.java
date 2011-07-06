package org.jbs.happysad;

import android.content.Context;

public class Syncer implements Runnable {

	private int myID;
	private boolean running;
	private HappyData h;
	public Syncer(int id, Context ctx){
		myID = id;
		running = true;
		h = new HappyData(ctx);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(running==true){
			try {
				Thread.sleep(10002); //update every 10 seconds
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			h.syncDown();
			h.syncUp();
			
		}
	}

	public void safeShutdown(){
		running = false;
	}
}
