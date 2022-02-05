package core.scheduler;

// 真正进行线程调度
public interface Worker  {
    void schedule(Runnable command);
}
