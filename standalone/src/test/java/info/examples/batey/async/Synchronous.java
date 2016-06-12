package info.examples.batey.async;

import info.examples.batey.async.thirdparty.*;
import org.junit.Test;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class Synchronous {

    private UserService users = UserService.userService();
    private ChannelService channels = ChannelService.channelService();
    private PermissionsService permissions = PermissionsService.permissionsService();

    /**
     * Show how the user service works
     */
    @Test
    public void userService() {
        assertNull("I don't expect charlie to exist", users.lookupUser("charlie"));

        assertEquals(new User("Christopher Batey", "chbatey"), users.lookupUser("chbatey"));
    }

    /**
     * Show how the permissions service works
     */
    @Test
    public void permissionsService() {
        assertNull("I don't expect charlie to have any permissions", permissions.permissions("charilie"));

        assertEquals(Permissions.permissions("ENTS", "SPORTS"), permissions.permissions("chbatey"));
    }

    /**
     * Show how the ChannelService works
     */
    @Test
    public void channelService() {
        assertNull("No channel named charlie", channels.lookupChannel("charlie"));

        assertEquals(new Channel("SkySportsOne"), channels.lookupChannel("SkySportsOne"));
    }

    /**
     * Scenario:
     * A web request comes in asking if chbatey has the SPORTS permission
     * <p>
     * Questions:
     * - Does the user exist?
     * - Is the user allowed to watch the channel?
     */
    @Test
    public void chbatey_has_sports() throws Exception {
        boolean hasSportsPermission = false;

        User user = users.lookupUser("chbatey");
        Permissions p = permissions.permissions(user.getUserId());
        hasSportsPermission = p.hasPermission("SPORTS");

        assertTrue(hasSportsPermission);
    }

    /**
     * Scenario:
     * A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions:
     * - Does this channel exist?
     * - Is chbatey a valid user?
     * - Does chbatey have the permissions to watch Sports?
     */
    @Test
    public void chbatey_watch_sky_sports_one() {
        Channel channel = null;
        User user = null;
        Permissions p = null;

        user = users.lookupUser("chbatey");             // ~100ms
        p = permissions.permissions(user.getUserId());  // ~100ms
        channel = channels.lookupChannel("SkySportsOne");  // ~100ms

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(user);
    }

    /**
     * Scenario:
     * A web request comes in asking of chbatey can watch SkySportsOne
     * <p>
     * Questions:
     * - Does this channel exist?
     * - Is chbatey a valid user?
     * - Does chbatey have the permissions to watch Sports?
     * <p>
     * Take a 2/3 of the response time.
     */
    @Test
    public void chbatey_watch_sky_sports_one_fast() throws Exception {
        Channel channel = null;
        User user = null;
        Permissions p = null;

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Channel> channelCallable = es.submit(() -> channels.lookupChannel("SkySportsOne"));
        user = users.lookupUser("chbatey");
        p = permissions.permissions(user.getUserId());
        channel = channelCallable.get();

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(user);
    }

    /**
     * Do all of the above but also time out if we don't get all the results back
     * within 500 milliseconds
     */
    @Test
    public void chbatey_watch_sky_sports_one_timeout() throws Exception {
        Result result = null;

        ExecutorService es = Executors.newCachedThreadPool();
        Future<Result> wholeOperation =  es.submit(() -> {
            Future<Channel> channelCallable = es.submit(() -> channels.lookupChannel("SkySportsOne"));
            User chbatey = users.lookupUser("chbatey");
            Permissions p = permissions.permissions(chbatey.getUserId());
            try {
                Channel channel = channelCallable.get();
                return new Result(channel, p);
            } catch (Exception e) {
                throw new RuntimeException("Oh dear", e);
            }
        });
        result = wholeOperation.get(500, TimeUnit.MILLISECONDS);

        assertNotNull(result.channel);
        assertTrue(result.permissions.hasPermission("SPORTS"));
    }
}
