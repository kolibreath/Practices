package core;

public class ObservableMap<T, U> extends AbstractObservableWithUpStream<T, U>{

    // 目的是将T类型转换成U类型
    final Function<T, U> mapper;
    //
    final ObservableSource<T> source;

    public ObservableMap(ObservableSource<T> source, Function<T, U> mapper) {
        super(source);
        this.source = source;
        this.mapper = mapper;
    }

    // TODO
    @Override
    void subscribeActual(Observer<U> observer) {
        MapObserver<T, U> mapObserver = new MapObserver<>(mapper, observer);
        // 重新调用Observable#subscribe，传入装饰后的Observer
        source.subscribe(mapObserver);
    }


    static class MapObserver<T, U> implements Observer<T> {
        private final Function<T, U> mapper;
        // 从回调中传入的observer
        private final Observer<U> downStream;

        public MapObserver(Function<T, U> mapper, Observer<U> observer) {
            this.mapper = mapper;
            this.downStream = observer;
        }

        // 其他的正常传递，除了在onNext中需要进行转换
        @Override
        public void onSubscribe() {
            downStream.onSubscribe();
        }

        @Override
        public void onNext(T t) {
            downStream.onNext( mapper.apply(t));
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
}
