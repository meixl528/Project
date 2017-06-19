package com.ssm.adaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISaveIpAddressListener extends Comparable<ISaveIpAddressListener>{
	
	void onSaveIpAdress(HttpServletRequest request, HttpServletResponse response);

    default int getOrder() {return 10;}

    @Override
    default int compareTo(ISaveIpAddressListener o) {
        return getOrder() - o.getOrder();
    }

}
