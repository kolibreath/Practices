package core;

public class ObservableCreate<T> extends Observable<T> {
    ObservableOnSubscribe<T> source;

    public ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    void subscribeActual(Observer<T> observer) {
     // 具体的create操作
        observer.onSubscribe();
        CreateEmitter<T> emitter = new CreateEmitter<>(observer);
        // 这样emitter就可以传递到回调中，调用onNext onComplete等操作
        source.subscribe(emitter);
    }

    // 其实具体的被观察者通知观察者的任务交给了Emitter
    static class CreateEmitter<T> implements Emitter<T> {
        // 通过done控制 后面的事件内容T 能够继续传递
        boolean done = false;
        Observer<T> observer;

        public CreateEmitter(Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onNext(T t) {
            if (done) return;
            observer.onNext(t);
        }

        @Override
        public void onComplete() {
            if (done) return;
            observer.onComplete();
            done = true;
        }

        @Override
        public void onError(Throwable throwable) {
            if (done) return;
            observer.onError(throwable);
            done = true;
        }
    }
}
