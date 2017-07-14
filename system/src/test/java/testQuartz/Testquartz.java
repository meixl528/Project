package testQuartz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class Testquartz {

	
	public static void main(String[] args) {
		
		ApplicationContext ctx= new ClassPathXmlApplicationContext(
				"file:/E:/eclipse/testWorkSpace/system/target/classes/applicationContext*.xml");
		
		SchedulerFactoryBean springScheduler = (SchedulerFactoryBean)ctx.getBean("quartzScheduler");
		
		
		
		
	}
}
