package com.ssm.interfaces.dto;

import java.util.List;

/**
 * @name        InterfaceRequest
 * @description soap报文请求参数
 * @author      meixl
 * @date        2017年6月27日下午4:29:19
 */
public class InterfaceRequest {
	
	private Element elements;
	
	public Element getElements() {
		return elements;
	}

	public void setElements(Element elements) {
		this.elements = elements;
	}

	public List<Data> getElementsList(){
		return this.elements.getElement();
	}
}

class Element{
	List<Data> element;

	public List<Data> getElement() {
		return element;
	}

	public void setElement(List<Data> element) {
		this.element = element;
	}
}