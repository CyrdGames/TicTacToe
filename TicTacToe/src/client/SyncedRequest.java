package client;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncedRequest {
    
    public Lock lock;
    public Condition actionOccurred;
    public String request; 
    
    public SyncedRequest(){
        this.lock = new ReentrantLock();
        this.actionOccurred = this.lock.newCondition();
        this.request = null;
    }
}
