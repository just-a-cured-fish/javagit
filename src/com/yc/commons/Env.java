package com.yc.commons;

import java.io.IOException;
import java.util.Properties;

public class Env extends Properties{//要导入两个jar包commons-dbcp.jar//commons-pool.jar
	private static Env instance=new Env();
	private Env() {
		//instance.load(new FileInputStream(new File("scr/db.properties")));//相对路径创建
		try {
			//加载配置文件
			this.load(Env.class.getClassLoader().getResourceAsStream("db.properties"));
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static Env getInstance() {
		return instance;
	}
}
