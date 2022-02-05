package core;

// 提供Emitter的实例
public interface ObservableOnSubscribe<T> {

    void subscribe(Emitter<T> emitter);
}
