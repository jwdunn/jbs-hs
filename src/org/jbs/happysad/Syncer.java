package org.jbs.happysad;

import android.content.Context;

public class Syncer implements Runnable {
	private UIDhelper UIDh = new UIDhelper();
	protected long myID;
	private HappyData h;
	public Syncer(Context ctx){
		myID = UIDh.getUID();
		h = new HappyData(ctx);
	}
	
	
	@Override
	/**
	 * Syncer uploads any bottles that haven't been uploaded yet, waits, then downloads all bottles of this user.
	 */
	public void run() {
		//first, upload all bottles that I've created locally that I haven't sent to the server.
		//notice that we aren't just uploading this latest bottle. This is a great thing, because
		//that way, any previous updates that you made that you haven't been able to upload (due to
		//no network)
		h.syncUp();
		//and now we get previous bottles of ours that aren't on our local db for some reason. 
		//we can sleep for a while before we do this, though, so that the dashboard (which should show up
		//right around when this thread starts running) can do its thang first.
		try{
			Thread.sleep(1000);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		h.syncMyDown();
	}

	
}
