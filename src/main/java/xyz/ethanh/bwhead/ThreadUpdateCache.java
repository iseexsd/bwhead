package xyz.ethanh.bwhead;

public class ThreadUpdateCache implements Runnable{
    @Override
    public void run() {
        BWHeadUtil.updateCache();
    }
}
