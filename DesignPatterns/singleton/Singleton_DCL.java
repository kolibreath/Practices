package singleton;

public class Singleton_DCL {

    private static volatile Singleton_DCL instance = null;

    private Singleton_DCL() {}

    public static Singleton_DCL getInstance() {
        if (instance == null) {
            synchronized (Singleton_DCL.class) {
                if (instance == null) {
                    instance = new Singleton_DCL();
                }
            }
        }
        return instance;
    }
}
