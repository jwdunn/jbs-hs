package org.jbs.happysad;

import android.content.Context;

public class Syncer implements Runnable {
	private UIDhelper UIDh = new UIDhelper();
	protected long myID;
	private boolean running;
	private HappyData h;
	public Syncer(Context ctx){
		myID = UIDh.getUID();
		running = true;
		h = new HappyData(ctx);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(running==true){
			
			
			h.syncDown();
			h.syncUp();
			
			try {
				Thread.sleep(10002); //update every 10 seconds
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void safeShutdown(){
		running = false;
	}
}
