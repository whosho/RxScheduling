package pl.barski.libraries.rxscheduler;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.concurrent.TimeUnit;

public interface ApplicationScheduler {

    <T> void schedule(Observable<T> observable, Action1<T> onNextAction, Object subscriber);

    <T> void schedule(Observable<T> observable, Action1<T> onNextAction, Action1<Throwable> onError, Object subscriber);

    <T> void schedule(Observable<T> observable, Action1<T> onNextAction, Action1<Throwable> onError, Action0 onFinished, Object subscriber);

    void schedule(long delay, TimeUnit timeUnit, Action0 onNextAction, Object subscriber);

    void unsubscribeTaskByTag(Object subscriber);

    void schedule(Subscription subscription, Object placePresenter);
}
