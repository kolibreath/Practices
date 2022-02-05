package core;

public abstract class AbstractObservableWithUpStream<T, U> extends Observable<U> {
    final ObservableSource<T> source;

    public AbstractObservableWithUpStream(ObservableSource<T> source) {
        this.source = source;
    }
}
