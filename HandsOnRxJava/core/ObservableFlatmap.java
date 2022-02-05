package core;

public class ObservableFlatmap<T, R> extends AbstractObservableWithUpStream<T, R> {
    private Function<T, Observable<R>> mapper;
    private ObservableSource<T> source;

    public ObservableFlatmap(ObservableSource<T> source, Function<T, Observable<R>> mapper) {
        super(source);
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    void subscribeActual(Observer<R> observer) {
        ObserverFlatmap<T, R> obf = new ObserverFlatmap<>(observer, mapper);
        // 逻辑流程：事件的传递是通过subscribe展开的
        // Emitter#onNext ->  ObserverFlatmap#onNext -> Observer(lambda)#onNext
        source.subscribe(obf);
    }

    static class ObserverFlatmap<T, R> implements Observer<T> {
        private final Observer<R> downStream;
        private final Function<T, Observable<R>> mapper;

        public ObserverFlatmap(Observer<R> downStream, Function<T, Observable<R>> mapper) {
            this.downStream = downStream;
            this.mapper = mapper;
        }

        @Override
        public void onSubscribe() {
        }

        @Override
        public void onNext(T t) {
            Observable<R> ob = mapper.apply(t);
            ob.subscribe(downStream);
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void onError(Throwable throwable) {
        }
    }

}
