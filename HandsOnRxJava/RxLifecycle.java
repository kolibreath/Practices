import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;

public class RxLifecycle<T> implements LifecycleObserver, ObservableTransformer<T, T> {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    // 模拟注解
    void onDestroy() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @NonNull
    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> observable) {
        // 在subscribe 之前处理
        return observable.doOnSubscribe(compositeDisposable::add);
    }

    // 注册到
    public static <T> RxLifecycle<T> bind(LifecycleOwner owner) {
        RxLifecycle<T> lifecycle = new RxLifecycle<>();
        owner.add(lifecycle);
        return lifecycle;
    }

    // usage
    private void test() {
        LifecycleOwner lifecycleOwner = lifecycleObserver -> { };

        Observable.just("1")
                .compose(RxLifecycle.bind(lifecycleOwner))
                .subscribe();
    }
}
