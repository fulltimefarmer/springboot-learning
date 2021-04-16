package com.nike.gcsc.auth.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Slf4j
public class SnowFlake {

    private final static long TWEPOCH = 12888349746579L;
    /**
     * Machine Identification Number
     */
    private final static long WORKERIDBITS = 5L;
    /**
     * Data Center Identification Number
     **/
    private final static long DATACENTER_ID_BITS = 5L;

    /**
     * Self-incremental digits in milliseconds
     **/
    private final static long SEQUENCE_BITS = 12L;
    /**
     * Machine ID offset 12 bits to the left
     **/
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * Data Center ID Left Shift 17 Bits
     **/
    private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKERIDBITS;
    /**
     * Time millisecond left shift 22 bits
     **/
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKERIDBITS + DATACENTER_ID_BITS;
    /**
     * Sequence mask to ensure that sequnce does not exceed the upper limit
     **/
    private final static long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
    /**
     * Last timestamp
     **/
    private static long lastTimestamp = -1L;
    /**
     * sequence
     **/
    private long sequence = 0L;
    /**
     * Server ID
     **/
    private long workerId = 1L;
    private static long workerMask = -1L ^ (-1L << WORKERIDBITS);
    /**
     * Process coding
     **/
    private long processId = 1L;
    private static long processMask = -1L ^ (-1L << DATACENTER_ID_BITS);

    private static SnowFlake snowFlake = null;

    static {
        snowFlake = new SnowFlake();
    }

    public static synchronized long nextId() {
        return snowFlake.getNextId();
    }

    private SnowFlake() {

        //Getting Machine Coding
        this.workerId = this.getMachineNum();
        //Getting process coding
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        this.processId = Long.valueOf(runtimeMxBean.getName().split("@")[0]).longValue();

        //Avoid Coding Beyond Maximum
        this.workerId = workerId & workerMask;
        this.processId = processId & processMask;
    }

    public synchronized long getNextId() {
        //Get the timestamp
        long timestamp = timeGen();
        //Error reporting if the timestamp is less than the last timestamp
        if (timestamp < lastTimestamp) {
            try {
                throw new Exception("Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //If the timestamp is the same as the last timestamp
        if (lastTimestamp == timestamp) {
            // In the current milliseconds, +1, and sequenceMask ensure that the sequence does not exceed the upper limit
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                //If the current millisecond count is full, wait for the next second
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        // The ID offset combination generates the final ID and returns the ID
        long nextId = ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) | (processId << DATACENTER_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
        return nextId;
    }

    /**
     * Get the timestamp again until it is different from the existing timestamp
     *
     * @param lastTimestamp
     * @return Next timestamp
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * Getting Machine Coding
     *
     * @return
     */
    private long getMachineNum() {
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            log.error("SnowFlak getMachineNum error", e1);
        }
        if (null != e) {
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
            }
        }
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }
}
