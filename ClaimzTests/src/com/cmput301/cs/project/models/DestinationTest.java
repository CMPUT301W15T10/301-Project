package com.cmput301.cs.project.models;

import com.google.android.gms.maps.model.LatLng;
import junit.framework.TestCase;

public class DestinationTest extends TestCase {
    public void testBuild() {
        final String name = "name";
        final String reason = "reason";
        final LatLng location = new LatLng(1.0, 1.0);

        Destination.Builder builder = new Destination.Builder().name(name).reason(reason).location(location);
        Destination dest = builder.build();

        assertEquals(builder.getName(), name);
        assertEquals(builder.getReason(), reason);
        assertEquals(builder.getLocation(), location);

        assertEquals(dest.getName(), name);
        assertEquals(dest.getReason(), reason);
        assertEquals(dest.getLocation(), location);
    }

}