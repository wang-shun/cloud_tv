package com.letv.portal.model;

import com.letv.common.model.BaseModel;
   
/**Program Name: BaseElementModel <br>
 * Description:  基础资源元素<br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年7月27日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public class BaseElement extends BaseModel{

	private static final long serialVersionUID = 4187977926839991202L;
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
