package pl.barski.libraries.rxscheduler;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static pl.barski.libraries.rxscheduler.RxUtils.doNothing;
import static pl.barski.libraries.rxscheduler.RxUtils.ignore;

/**
 * The base implementation of application scheduler.
 */
public class BaseApplicationScheduler implements ApplicationScheduler {
    private final Map<String, List<Subscription>> subscriptions;
    private final Scheduler observingScheduler;
    private final Scheduler executingScheduler;

    public BaseApplicationScheduler(Scheduler observingScheduler, Scheduler executingScheduler) {
        this.observingScheduler = observingScheduler;
        this.executingScheduler = executingScheduler;
        subscriptions = new HashMap<>();
    }

    @Override
    public <T> void schedule(Observable<T> observable, Action1<T> onNextAction, Object subscriber) {
        schedule(observable, onNextAction, error -> doNothing(), ignore(), obtainTag(subscriber));
    }

    @Override
    public <T> void schedule(Observable<T> observable, Action1<T> onNextAction, Action1<Throwable> onError, Object subscriber) {
        schedule(observable, onNextAction, onError, ignore(), obtainTag(subscriber));
    }

    @Override
    public <T> void schedule(Observable<T> observable, Action1<T> onNextAction, Action1<Throwable> onError, Action0 onFinished, Object subscriber) {
        Subscription subscription = observable
                .observeOn(observingScheduler)
                .subscribeOn(executingScheduler)
                .subscribe(onNextAction, onError, onFinished);
        subscribe(subscription, subscriber);
    }

    @Override
    public void schedule(long delay, TimeUnit timeUnit, Action0 onNextAction, Object subscriber) {
        schedule(Observable.timer(delay, timeUnit).limit(1), value -> onNextAction.call(), error -> doNothing(), obtainTag(subscriber));
    }

    @Override
    public void unsubscribeTaskByTag(Object subscriber) {
        String tag = obtainTag(subscriber);
        if (subscriptions.containsKey(tag)) {
            for (Subscription subscription : subscriptions.get(tag)) {
                subscription.unsubscribe();
            }
            subscriptions.get(tag).clear();
        }
    }

    @Override
    public void schedule(Subscription subscription, Object subscriber) {
        subscribe(subscription, subscriber);
    }

    @Override
    public boolean keepsSubscription(Object subscriber) {
        String tag = obtainTag(subscriber);
        return subscriptions.containsKey(tag) && subscriptions.get(tag).size() > 0;
    }

    private boolean subscribe(Subscription subscription, Object subscriber) {
        String tag = obtainTag(subscriber);
        if (!subscriptions.containsKey(tag)) {
            subscriptions.put(tag, new ArrayList<>());
        }
        return subscriptions.get(tag).add(subscription);
    }

    private String obtainTag(Object subscriber) {
        return subscriber.toString();
    }

}
