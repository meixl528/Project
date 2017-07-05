package testLock;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.ssm.lock.DistributedLockCallback;
import com.ssm.lock.DistributedLockProvider;

/**
 * Thread.sleep(100)使线程1先执行,结果1可用,2阻塞
 * @name        TestLock
 * @description redis锁测试
 * @author      meixl
 * @date        2017年7月5日下午3:40:17
 * @version
 */
public class TestLock {

	@SuppressWarnings({ "rawtypes", "resource", "unchecked" })
	public static void main(String[] args) {
		URL url = TestLock.class.getClassLoader().getResource("applicationContext-core.xml");
		System.out.println("URL = "+url);
		
		ApplicationContext ctx= new ClassPathXmlApplicationContext(
				"file:/E:/eclipse/testWorkSpace/system/target/classes/applicationContext*.xml");
		
		DistributedLockProvider provider = (DistributedLockProvider)ctx.getBean("distributeLockTemplate");
		RedisTemplate<String, String> redisTemplate = (RedisTemplate)ctx.getBean("redisTemplate");
		try {
			ExecutorService pool= Executors.newFixedThreadPool(2);
			List<Future> list = new ArrayList<Future>();  
			for (int i = 0; i < 2; i++) {
				int lockNum = i+1;
				
				Thread.sleep(100);
				MyCallable myCallable = new MyCallable(provider,lockNum,redisTemplate);
				Future f = pool.submit(myCallable);
				list.add(f);
			}
			// 关闭线程池  
			pool.shutdown();  
			  
			// 获取所有并发任务的运行结果  
			for (int i = 0; i < list.size(); i++) {
				Future f  = list.get(i);
			    // 从Future对象上获取任务的返回值，并输出到控制台  
				System.out.println("result >>>" + f.get().toString());  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(provider!=null){
				provider.releaseLock("test123");
			}
		}
	}
}

class MyCallable implements Callable<String>{
	private DistributedLockProvider provider;
	private int lockNum;
	private RedisTemplate<String, String> redisTemplate;
	
	MyCallable(){}
	MyCallable(DistributedLockProvider provider,int lockNum,RedisTemplate<String, String> redisTemplate){
		this.provider = provider;
		this.lockNum = lockNum;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String call() throws Exception {
		System.out.println("lockNum : "+lockNum);
		boolean bool = provider.lock("test123");
		if(lockNum ==1){
			Thread.sleep(6000);
		}
		String message = "";
		if(!bool){
			message = lockNum +" 资源正忙----此数据正在被操作，请稍后!";
		}else{
			message = lockNum +" 可使用";
			redisTemplate.opsForValue().set("test123", "redisLockTest:"+lockNum);
			provider.releaseLock("test123");
		}
		System.out.println(message);
		
		return message;
		//return provider.lock("test123", callback);
	}
	
}

class DoProgress implements DistributedLockCallback<String>{
	private int lockNum;
	private RedisTemplate<String, String> redisTemplate;
	
	DoProgress(){}
	DoProgress(int lockNum,RedisTemplate<String, String> redisTemplate){
		this.lockNum = lockNum;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String process() throws Exception {
		System.out.println(lockNum);
		if(lockNum==1){
			//Thread.sleep(6000);
		}
		redisTemplate.opsForValue().set("test123", "redisLockTest:"+lockNum);
		return lockNum +" ok";
	}

}
