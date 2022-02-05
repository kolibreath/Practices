package core;

import core.scheduler.Scheduler;
import core.scheduler.Worker;

import java.util.ArrayDeque;
import java.util.Deque;

// 目标：ObserveOn需要执行观察者接收事件所在的线程
public class ObservableObserveOn<T> extends AbstractObservableWithUpStream<T, T>{
    private final Scheduler scheduler;

    public ObservableObserveOn(ObservableSource<T> source, Scheduler scheduler) {
        super(source);
        this.scheduler = scheduler;
    }

    @Override
    void subscribeActual(Observer<T> observer) {
        Worker worker = scheduler.createWorker();
        source.subscribe(new ObserveOnObserver<T>(worker, observer));
    }

    // 1. 首先需要将观察者实现Runnable，以便切换到Scheduler指定的线程
    private static final class ObserveOnObserver<T> implements Observer<T>, Runnable {
        private final Worker worker;
        private final Observer<T> downStream; // 被修饰的Observer
        private final Deque<T> deque = new ArrayDeque<>(); // 存放事件的队列

        private volatile boolean done;
        private volatile Throwable error;
        private volatile boolean over;

        public ObserveOnObserver(Worker worker, Observer<T> downStream) {
            this.worker = worker;
            this.downStream = downStream;
        }

        @Override
        public void run() {
            // 从队列中获取事件 并且处理
            while (true) {
                T t = deque.poll(); // returns null when the queue is empty
                boolean empty = t == null;
                if (checkTerminated(done, empty, downStream)) {
                    return;
                }
                if (empty)
                    break;
                downStream.onNext(t);
            }
        }

        private boolean checkTerminated(boolean done, boolean empty, Observer<T> downStream) {
            if (over) {
                deque.clear();
                return true;
            }

            if (done) {
                // 如果抛出异常
                if (error != null) {
                    over = true;
                    downStream.onError(error);
                    return true;
                } else if (empty) {
                    over = true;
                    downStream.onComplete();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onSubscribe() {
            downStream.onSubscribe();
        }

        @Override
        public void onNext(T t) {
            // 对于传入的事件 先存放到队列中
            deque.offer(t);
            // 指定run定义的操作
            worker.schedule(this);
        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable throwable) {

        }
    }
}
