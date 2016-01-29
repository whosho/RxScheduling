package pl.barski.libraries.rxscheduler;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static pl.barski.libraries.rxscheduler.RxUtils.doNothing;

public class BaseApplicationSchedulerTest {

    private ApplicationScheduler systemUnderTest;

    @Before
    public void setUp() throws Exception {
        systemUnderTest = new BaseApplicationScheduler(Schedulers.immediate(), Schedulers.immediate());
    }

    @Test
    public void shouldKeepSubscriptionsByTag() throws Exception {
        assertFalse(systemUnderTest.keepsSubscription(this));
        assertFalse(systemUnderTest.keepsSubscription("ANY-TAG"));

        systemUnderTest.schedule(Observable.never(), result -> doNothing(), this);

        assertTrue(systemUnderTest.keepsSubscription(this));
        assertFalse(systemUnderTest.keepsSubscription("ANY-TAG"));
    }

    @Test
    public void shouldKeepSubscriptionsAfterTaskCompleted() throws Exception {
        assertFalse(systemUnderTest.keepsSubscription(this));

        systemUnderTest.schedule(Observable.just(0), result -> doNothing(), this);

        assertTrue(systemUnderTest.keepsSubscription(this));
    }

    @Test
    public void shouldNotKeepSubscriptionsAfterUnsubscribedByTag() throws Exception {
        systemUnderTest.schedule(Observable.just(0), result -> doNothing(), this);

        systemUnderTest.unsubscribeTaskByTag(this);

        assertFalse(systemUnderTest.keepsSubscription(this));
    }
}