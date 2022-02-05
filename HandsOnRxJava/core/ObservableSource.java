package core;

public interface ObservableSource<T> {

    // addObserver
    void subscribe(Observer<T> observer);
}
