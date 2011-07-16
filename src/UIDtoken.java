
public enum UIDtoken {

    INSTANCE;
    private Long UID;


    public void setUID(Long myID){
    	UID = myID;
    }
    
    public Long getAge() {
        return UID;
    }
}