package singleton;

public class Singleton_hunger {
    // init singleton once the class is loaded
    private static final Singleton_hunger instance = new Singleton_hunger();

    private Singleton_hunger() {}

    private static Singleton_hunger getInstance() {
        return instance;
    }
}
