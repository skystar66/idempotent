package com.tbex.idmpotent.server.utils;


import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName: MySeqIdGen
 * @Description: todo 生成全局唯一序列号工具类
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:32
 * @Version: 1.0
 */
public class MySeqIdGen {


    public static AtomicLong default_seq = new AtomicLong(0L);
    public static String default_seq_prefix = "USDT";


    //节点编号
    public static String node = "00";

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

    public static String getId() {
        return getSeq(default_seq_prefix, default_seq);
    }

    private static String getSeq(String prefix, AtomicLong seq) {
        prefix += node;
        //预留八位增长空间
        return String.format("%s%s%08d", prefix, System.currentTimeMillis(), (int) seq.getAndIncrement() % 1000000);
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

        System.out.println(getId());


    }

}