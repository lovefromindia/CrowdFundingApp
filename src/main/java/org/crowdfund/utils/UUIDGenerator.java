package org.crowdfund.utils;

import java.util.concurrent.atomic.AtomicLong;

public class UUIDGenerator {

    private UUIDGenerator(){}
    private static AtomicLong uuidGenerator = new AtomicLong(0);

    public static Long getUUID()
    {
        return uuidGenerator.incrementAndGet();
    }

}
