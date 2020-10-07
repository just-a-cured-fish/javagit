package com.yc.commons;

import java.io.IOException;
import java.util.Properties;

/**
 * �̳�  ����ģʽ
 * ����������Ϣ
 * @author wong
 *
 */
public class MyProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static MyProperties instance=new MyProperties();
	//���캯��˽�л�
	private MyProperties(){
		//���������ļ�
		//instance.load(new FileInputStream("src/db.properties"));//���·������
		//���������ȡ��Ϣ
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
