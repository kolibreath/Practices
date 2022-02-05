package core;

// 在原本的观察者模式中，这些操作对应的是具体的被观察者的动作
// 被观察者发生这些动作，就回调用对应的方法
public interface Observer<T> {
    void onSubscribe();

    void onNext(T t);

    void onComplete();

    void onError(Throwable throwable);
}
