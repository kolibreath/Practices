package singleton;

public class Singleton_lazy {
    // load Singleton lazily when the singleton is used first time
    private Singleton_lazy instance = null;

    private Singleton_lazy() {}

    // synchronized for concurrency
    private synchronized Singleton_lazy getInstance() {
        if (instance == null)
            instance = new Singleton_lazy();
        return instance;
    }
}
