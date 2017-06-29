package com.ssm.interfaces.dto;
public class Data{
		
		private String userName;
		
		private String phone;
		
		public Data() {}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		@Override
		public String toString() {
			return "Data [userName=" + userName + ", phone=" + phone + "]";
		}
		
	}