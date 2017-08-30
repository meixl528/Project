package com.ssm.interfaces.components;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ssm.message.profile.GlobalProfileListener;

@Component
public class Token implements GlobalProfileListener{
	
//	private String authBaseURL;
//	private String dataBaseURL;
//	private String clientId;
//	private String clientSecret;
//	private String clientAuthKey;
	
	public static String name;
	public static String pass;
	
	@Override
	public List<String> getAcceptedProfiles() {
		return Arrays.asList("itf_token");
	}
	@Override
	public void updateProfile(String profileName, String profileValue) {
		String[] values = profileValue.split(",");
		if(values.length == 2){
			name = values[0];
			pass = values[1];
		}
		
		//
		if(StringUtils.isBlank(name) && StringUtils.isBlank(pass)){
			name = "meixl";
			pass = "meixl";
		}
	}
	

}
