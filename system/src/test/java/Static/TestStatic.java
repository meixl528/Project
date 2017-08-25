package Static;

import testJDBC.User;

/**
 * 静态代码块和静态方法不能使用 本类的<b> 非静态 </b>成员变量及方法
 * <br>
 * 但可以使用其他类的
 * @name        TestStatic
 * @description static测试
 * @author      meixl
 * @date        2017年8月25日下午4:29:17
 * @version
 */
public class TestStatic {
	
	static {
		User user = new User("123","456");
		System.out.println(user.toString());
	}
	
	public static void get(){
        User user = new User("789","456");
		System.out.println(user.toString());
	}

	
	public static void main(String[] args) {
		// TestStatic.get();
	}
	
}
