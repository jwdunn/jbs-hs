package org.jbs.happysad;

public enum UIDtoken {

    INSTANCE;
    private Long UID = (long) -7;


    public void setUID(long myID){
    	UID = myID;
    }
    
    public Long getUID() {
        return UID;
    }
    
    
}