package com.tbex.idmpotent.client.utils;


import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName: MySeqIdGen
 * @Description: todo 生成全局唯一序列号工具类
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:32
 * @Version: 1.0
 */
public class MySeqIdGen {

    private static AtomicLong admin_seq = new AtomicLong(0L);
    private static String admin_seq_prefix = "ADMIN";
    private static AtomicLong usdt_seq = new AtomicLong(0L);
    private static String usdt_seq_prefix = "USDT";
    private static AtomicLong mix_seq = new AtomicLong(0L);
    private static String mix_seq_prefix = "MIX";

    //节点编号
    private static String node = "00";

    static {
        try {
            //URL url = Thread.currentThread().getContextClassLoader().getResource("config" + File.separator + "system.properties");
            //Properties properties = new Properties();
            //properties.load(url.openStream());
            //node = properties.getProperty(ConfigEnum.SERVER_NAME.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUsdtId() {
        return getSeq(usdt_seq_prefix, usdt_seq);
    }

    public static String getAdminId() {
        return getSeq(admin_seq_prefix, admin_seq);
    }

    public static String getMixId() {
        return getSeq(mix_seq_prefix, mix_seq);
    }

    private static String getSeq(String prefix, AtomicLong seq) {
        prefix += node;
        //预留八位增长空间
        return String.format("%s%s%08d", prefix, DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
    }

    public static void main(String[] args) {
//		for (int i = 0; i < 100; i++) {
//			System.out.println("pay=" + getPay());
//			System.out.println("trans=" + getTrans());
//			System.out.println("refund=" + getRefund());
//		}
//		pay_seq_prefix += node;
//			String str =   String.format("%s%s%06d", pay_seq_prefix, DateUtil.getSeqString(), (int) pay_seq.getAndIncrement() % 1000000);
//			String str1 =   String.format("%08d",(int) pay_seq.getAndIncrement() % 1000000);
//        int num = (int) pay_seq.getAndIncrement();
//		System.out.println(str+"----->>" +str1);


    }

}