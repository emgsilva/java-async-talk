package info.examples.batey.async;

import info.examples.batey.async.thirdparty.*;
import org.junit.Test;

import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class VanillaFutures {

    private UserService users = UserService.userService();
    private ChannelService channels = ChannelService.channelService();
    private PermissionsService permissions = PermissionsService.permissionsService();

    /**
     * Scenario:
     * A web request comes in asking if chbatey has the SPORTS permission
     * <p>
     * Questions:
     * - Does the user exist?
     * - Is the user allowed to watch the channel?
     */
    @Test
    public void chbatey_has_sports_blocking() throws Exception {
        boolean hasSportsPermission = false;

        Future<User> fUser = users.lookupUserAsync("chbatey");

        // Make the blocking explicit
        User chbatey = fUser.get();

        Future<Permissions> fPermission = permissions.permissionsAsync(chbatey.getUserId());

        // Explicit blocking
        Permissions p = fPermission.get();

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
    public void chbatey_watch_sky_sports_one_blocking() throws Exception {
        Future<User> fUser = users.lookupUserAsync("chbatey");

        // Make the blocking explicit
        User chbatey = fUser.get();

        Future<Permissions> fPermissions = permissions.permissionsAsync(chbatey.getUserId());

        // Explicit blocking
        Permissions p = fPermissions.get();

        Future<Channel> fChannel = channels.lookupChannelAsync("SkySportsOne");

        // Explicit blocking
        Channel channel = fChannel.get();

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(chbatey);

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
    public void chbatey_watch_sky_sports_one_concurrent() throws Exception {
        Future<Channel> fChannel = channels.lookupChannelAsync("SkySportsOne");

        Future<User> fUser = users.lookupUserAsync("chbatey");

        // Make the blocking explicit
        User chbatey = fUser.get();

        Future<Permissions> fPermissions = permissions.permissionsAsync(chbatey.getUserId());

        // Explicit blocking
        Permissions p = fPermissions.get();

        // Explicit blocking
        Channel channel = fChannel.get();

        assertNotNull(channel);
        assertTrue(p.hasPermission("SPORTS"));
        assertNotNull(chbatey);

    }

    @Test
    public void chbatey_watch_sky_sports_one_concurrent_no_blocking() throws Exception {
        Future<Channel> fChannel = channels.lookupChannelAsync("SkySportsOne");
        Future<User> fUser = users.lookupUserAsync("chbatey");
        // ??
        //Future<Permissions> pFuture = permissions.permissionsAsync(chbatey.getUserId());
    }
}
