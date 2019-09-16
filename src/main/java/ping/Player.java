package ping;

public class Player implements Runnable {
    private static final int MAX_COUNT = 10;
    private final String sound;
    private final boolean turn;
    private String lock = "";
    private Player partner;


    public Player(String sound, boolean turn) {
        this.sound = sound;
        this.turn = turn;
    }

    public Player setPartner(Player partner) {
        this.partner = partner;
        return this;
    }

    public String getLock() {
        return lock;
    }

    @Override
    public void run() {
        for (int i = 0; i < MAX_COUNT; i++) {
            synchronized (lock) {
                if (PingAndPong.turn != this.turn) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(sound);
                PingAndPong.turn = !this.turn;
                partner.getLock().notifyAll();
            }
        }
    }
}
