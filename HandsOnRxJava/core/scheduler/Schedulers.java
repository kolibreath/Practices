package core.scheduler;


// 提供单例
public class Schedulers {

    private static Scheduler cachedPool = null;
    private static Scheduler main = null;

    static {
        main = new MainScheduler();
        cachedPool = new ExecutorScheduler();
    }

    public static Scheduler main() { return main; }
    public static Scheduler cachedPool() {
        return cachedPool;
    }
}
