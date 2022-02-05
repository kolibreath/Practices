package core;

public interface Emitter<T> {

    void onNext(T t);

    void onComplete();

    void onError(Throwable throwable);

}
