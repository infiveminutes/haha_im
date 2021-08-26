package com.haha.im.utils;


import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SnowFlakeWorker {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1607155485176L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long dataCenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //控制序列号
    private long actualSequence = 0L;  // 实际使用的序列号
    private long lastStmp = -1L;//上一次时间戳

    public SnowFlakeWorker() {

        this.dataCenterId = getDatacenterId(MAX_DATACENTER_NUM);
        this.machineId = getMaxWorkerId(dataCenterId, MAX_MACHINE_NUM);
    }

    public SnowFlakeWorker(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATACENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }


    /**
     * <p>
     * 数据标识id部分
     * </p >
     */
    protected static long getDatacenterId(long maxDatacenterId) {

        try {

            long id = 0L;

            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }

            return id;

        } catch (SocketException e) {
            // log.warn("SocketException for getDatacenterId: " + e.getMessage());
            throw new RuntimeException("SocketException for getDatacenterId: " + e.getMessage(), e);
        } catch (UnknownHostException e) {
            // log.warn("UnknownHostException for getDatacenterId: " + e.getMessage());
            throw new RuntimeException("UnknownHostException for getDatacenterId: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * 获取 maxWorkerId
     * </p >
     */
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name != null && !name.isEmpty()) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 产生下一个ID
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewStmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        /**
         * 时间不连续出来全是偶数
         */
        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }
        // 上面那个控制序列号sequence，控制了一毫秒内不会超过MAX_SEQUENCE(4096)个，超过会等待，直到下一毫秒才会继续
        // 上面那个序列号sequence如果一毫秒只生成一个id，那么它永远都是0，那么取模永远都是0，插入的表也就可以理解为都是0表（或规律的那几张表），达不到均匀分布在各表的目的
        // 所以用下面这个序列号actualSequence来生成均匀的取模id，达到均匀分布在各表的目的
        actualSequence = (actualSequence + 1) & MAX_SEQUENCE;

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | dataCenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | actualSequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewStmp();
        while (mill <= lastStmp) {
            mill = getNewStmp();
        }
        return mill;
    }

    private long getNewStmp() {
        return System.currentTimeMillis();
    }

}