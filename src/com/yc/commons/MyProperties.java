package com.yc.commons;

import java.io.IOException;
import java.util.Properties;

/**
 * 继承  单例模式
 * 加载配置信息
 * @author wong
 *
 */
public class MyProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static MyProperties instance=new MyProperties();
	//构造函数私有化
	private MyProperties(){
		//加载配置文件
		//instance.load(new FileInputStream("src/db.properties"));//相对路径创建
		//类加载器获取信息
		try{
			this.load(MyProperties.class.getClassLoader().getResourceAsStream("db.properties"));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static MyProperties getInstance(){
		return instance;
	}
}
