package core;

import core.scheduler.Scheduler;

public abstract class Observable<T> implements ObservableSource<T>{
    @Override
    public void subscribe(Observer<T> observer) {
        subscribeActual(observer);
    }

    abstract void subscribeActual(Observer<T> observer);


    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        // source -> ObservableOnSubscribe
        return new ObservableCreate<>(source);
    }

    public <U> Observable<U> map(Function<T, U> function) {
        return new ObservableMap<>(this, function);
    }

    public <U> Observable<U> flatmap(Function<T, Observable<U>> function) {
        return new ObservableFlatmap<>(this, function);
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new ObservableSubscribeOn<>(this, scheduler);
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return new ObservableObserveOn<>(this, scheduler);
    }
}
