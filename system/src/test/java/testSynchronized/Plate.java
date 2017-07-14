package testSynchronized;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 生产者/消费者模式(线程同步模型)
 * @name        Plate
 * @description 测试线程同步锁
 * @author      meixl
 * @date        2017年7月13日上午9:32:06
 * @version
 * @see <a href="http://blog.csdn.net/haolongabc/article/details/7249098">
 * 			http://blog.csdn.net/haolongabc/article/details/7249098
 *      </a>
 */
public class Plate {
	Logger logger = LoggerFactory.getLogger(Plate.class);
	
	/**
	 * 对于public synchronized void add(int num)这种情况，意味着什么呢？其实这种情况，锁就是这个方法所在的对象。
	 * 
	 * 同理，如果方法是public  static synchronized void add(int num)，那么锁就是这个方法所在的class
	 */
 
	/**
	 * 一个线程执行临界区代码过程如下：
	 *	1 获得同步锁
	 *	2 清空工作内存
	 *	3 从主存拷贝变量副本到工作内存
	 *	4 对这些变量计算
	 *	5 将变量从工作内存写回到主存
	 *	6 释放锁
	 *	可见，synchronized既保证了多线程的并发有序性，又保证了多线程的内存可见性
	 */
    List<Object> eggs = new ArrayList<Object>();
 
    public synchronized Object getEgg() {
        while(eggs.size() == 0) {
            try {
            	logger.info("getEgg waiting ...");
                wait();
            } catch (InterruptedException e) {
            }
        }
 
        Object egg = eggs.get(0);
        eggs.clear();// 清空盘子
        notify();// 唤醒阻塞队列的某线程到就绪队列
        System.out.println("拿到鸡蛋");
        return egg;
    }
 
    public synchronized void putEgg(Object egg) {
        while(eggs.size() > 0) {
            try {
            	logger.info("putEgg waiting ...");
                wait();
            } catch (InterruptedException e) {
            }
        }
        eggs.add(egg);// 往盘子里放鸡蛋
        notify();// 唤醒阻塞队列的某线程到就绪队列
        System.out.println("放入鸡蛋");
    }
   
    static class AddThread extends Thread{
        private Plate plate;
        private Object egg=new Object();
        public AddThread(Plate plate){
            this.plate=plate;
        }
       
        public void run(){
            for(int i=0;i<5;i++){
                plate.putEgg(egg);
            }
        }
    }
   
    static class GetThread extends Thread{
        private Plate plate;
        public GetThread(Plate plate){
            this.plate=plate;
        }
       
        public void run(){
            for(int i=0;i<5;i++){
                plate.getEgg();
            }
        }
    }
   
    //main 测试
    public static void main(String args[]){
        try {
            Plate plate=new Plate();
            Thread add=new Thread(new AddThread(plate));
            Thread get=new Thread(new GetThread(plate));
            add.start();
            get.start();
            add.join();
            get.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("测试结束");
    }
    
}