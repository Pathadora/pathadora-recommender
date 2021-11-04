package server.stardog;

public class StardogRunnable implements Runnable {
    private final StardogDatabase database;

    public StardogRunnable(StardogDatabase db){
        this.database = db;
    }

    @Override
    public void run() { }

    public StardogDatabase database(){ return database;}
}
