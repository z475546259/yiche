package com.zzq.flow;

import com.zzq.entity.User;

public class SingleThread implements Runnable{
	public User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public SingleThread(User user) {
		super();
		this.user = user;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			SingleFlow flow = new SingleFlow();
			flow.start(user);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("线程报错了，捕获："+e.getMessage());
		}
//		Thread.currentThread().notifyAll();
	}
	
	

}
