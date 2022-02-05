package core.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutorScheduler extends Scheduler{
    @Override
    public Worker createWorker() {
        return new ExecutorWorker();
    }

    private static class ExecutorWorker implements Worker{
        private final ExecutorService service = Executors.newCachedThreadPool();

        @Override
        public void schedule(Runnable command) {
            service.execute(command);
            service.shutdown();
        }
    }
}
