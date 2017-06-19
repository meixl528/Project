package testDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

@SuppressWarnings("deprecation")
public class DateHelper {
	
	//public static Logger logger = LoggerFactory.getLogger(DateHelper.class);
	
	public static SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
	public static SimpleDateFormat formaterDetail = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static Calendar calendar = Calendar.getInstance();
	
	public static void main(String[] args) {
		
		DateHelper.MyDate date = new DateHelper().new MyDate(2012, 12, 12);
		DateHelper.MyDate dateTime = new DateHelper().new MyDate(2012, 12, 12, 0, 0, 0);
		DateHelper.MyDate time = new DateHelper().new MyDate(2012, 12, 12, 12, 12, 12);
		
		
		System.out.println(DateHelpFormat(date,0));
		System.out.println(DateHelpFormat(dateTime,0));
		System.out.println(DateHelpFormat(time,0));
		System.out.println();
		System.out.println(DateHelpFormat(date,1));
		System.out.println(DateHelpFormat(dateTime,1));
		System.out.println(DateHelpFormat(time,1));
		
	}
	
	public static String DateHelpFormat(Date date,int minOrMax){
		return formaterDetail.format(DateHelp(date,minOrMax));
	}
    
	/**
	 * 返回 00:00:00  或者  23:59:59
	 * @param minOrMax 0 或 1
	 * @param 设置日期为 时间 00:00:00 或 23:59:59
	 * @param date
	 */
	public static Date DateHelp(Date date,int minOrMax){
		if(date==null) 
			return date;
		try {
			if(IsDateNotTime(date)){
				if(minOrMax==0){
					return formaterDetail.parse(formater.format(date)+ " 00:00:00");
				}else if(minOrMax==1){
					return formaterDetail.parse(formater.format(date)+ " 23:59:59");
				}
			}else{
				return formaterDetail.parse(formaterDetail.format(date));
			}
		} catch (ParseException e) {
			/*if(logger.isErrorEnabled()){
				logger.error("date format failed");
			}*/
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 是date 不是  time (是日期不是时间)
	 * @param 是  yyyy/MM/dd 不是 yyyy/MM/dd HH:mm:ss
	 * @param 当 yyyy/MM/dd 00:00:00   也返回true
	 */
	public static boolean IsDateNotTime(Date date){
		return ( (date.getHours()==0) && (date.getMinutes()==0) && (date.getSeconds()==0) )?true:false;
	}
	
	
	public class MyDate extends Date{
		/**
		 */
		private static final long serialVersionUID = 1L;
		
		public int year;
		public int month;
		public int day;
		public int hours;
		public int minutes;
		public int seconds;
		
		public MyDate(){
			super();
		};
		
		public MyDate(int year,int month,int day){
			this(year,month,day,0,0,0);
		}
		
		public MyDate(int year,int month,int day,int hours,int minutes,int seconds){
			this.setYear(year-1900);
			this.setMonth(month-1);
			this.setDate(day);
			
			this.setHours(hours);
			this.setMinutes(minutes);
			this.setSeconds(seconds);
		}
		
	}

}
