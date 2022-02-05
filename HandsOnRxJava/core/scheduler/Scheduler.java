package core.scheduler;

// 提供Worker Worker是真正的进行线程调度的类
public abstract class Scheduler {
    public abstract Worker createWorker();
}
