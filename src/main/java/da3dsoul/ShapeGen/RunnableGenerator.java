package da3dsoul.ShapeGen;

/**
 * Created by Tom on 11/7/2015.
 */
public abstract class RunnableGenerator implements Runnable {

    protected final int X;
    protected final int Y;
    protected final int Z;

    public RunnableGenerator(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
    }

    public void run() {
        synchronized (this) {
            generate();
            notify();
        }
    }

    public abstract void generate();
}
