package core.scheduler;


//
public class MainScheduler extends Scheduler {
    @Override
    public Worker createWorker() {
        return new MainWorker();
    }

    private static class MainWorker implements Worker {
        @Override
        public void schedule(Runnable command) {
            // start a new Thread named MAIN
            new Thread(command, "MAIN").start();
        }
    }
}
