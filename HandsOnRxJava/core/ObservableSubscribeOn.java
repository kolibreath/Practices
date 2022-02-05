package core;

import core.scheduler.Scheduler;

// 修改被观察者向观察者发送消息的线程
// 不需要考虑类型
public class ObservableSubscribeOn<T> extends AbstractObservableWithUpStream<T, T> {
    private final Scheduler scheduler;

    public  ObservableSubscribeOn(ObservableSource<T> source, Scheduler scheduler) {
        super(source);
        this.scheduler = scheduler;
    }


    @Override
    void subscribeActual(Observer<T> observer) {
        // 这里的调用的线程和create的线程一致
        observer.onSubscribe();
        SubscribeOnObserver<T> sub = new SubscribeOnObserver<>(observer);
        scheduler.createWorker().schedule(new SubscribeTask(sub));
    }

    // 除了onSubscribe方法之外，其他的方法都需要重写，因为线程不一样
    // 实现SubscribeOnObserver的其他方法是为了避免onSubscribe调用两次
    private static class SubscribeOnObserver<T> implements Observer<T> {

        private final Observer<T> downStream;
        public SubscribeOnObserver(Observer<T> downStream) {
            this.downStream = downStream;
        }

        @Override
        public void onSubscribe() { }

        @Override
        public void onNext(T t) {
            downStream.onNext(t);
        }

        @Override
        public void onComplete() {
            downStream.onComplete();
        }

        @Override
        public void onError(Throwable throwable) {
            downStream.onError(throwable);
        }
    }

    private class SubscribeTask implements Runnable {

        private final Observer<T> observer;
        public SubscribeTask(Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void run() {
            source.subscribe(observer);
        }
    }
}
