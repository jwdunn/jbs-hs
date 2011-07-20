package org.jbs.happysad;

public enum UIDtoken {

    INSTANCE;
    private long UID = (long) -6;


    public void setUID(long myID){
    	UID = myID;
    }
    
    public Long getUID() {
        return UID;
    }
    
    
}