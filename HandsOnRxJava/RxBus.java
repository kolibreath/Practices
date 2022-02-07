import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    // 使用PublishSubject 避免粘性事件
    private final Subject<Object> subject;
    // 通过单例模式构建RxBus的实例
    private static class Holder {
        private static final RxBus bus = new RxBus();
    }

    private RxBus() {
        subject = PublishSubject.create().toSerialized();
    }

    public static  RxBus get() {
        return Holder.bus;
    }

    public void post(Object object) {
        subject.onNext(object);
    }

    public <T> Observable<T> toObservable(Class<T> clazz) {
        return subject.ofType(clazz);
    }
}
