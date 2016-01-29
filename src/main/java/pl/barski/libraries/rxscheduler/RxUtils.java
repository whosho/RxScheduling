package pl.barski.libraries.rxscheduler;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by greg on 1/29/16.
 */
public class RxUtils {

    public static Action1 doNothing() {
        return (arg) -> {
        };
    }

    public static Action0 ignore() {
        return () -> {
        };
    }

}
