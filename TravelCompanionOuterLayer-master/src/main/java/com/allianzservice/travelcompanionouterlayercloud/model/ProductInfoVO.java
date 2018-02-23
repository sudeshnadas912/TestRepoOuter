/*******************************************************************************
 * Copyright 2018 TCS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.allianzservice.travelcompanionouterlayercloud.model;

import java.util.List;
import static java.util.stream.Collectors.toList;

import org.springframework.stereotype.Component;

@Component
public class ProductInfoVO 
{

	
	private String productName;
	private List<PackageinfoVO> packageList;
	public ProductInfoVO(){
		//Do Nothing
	}
	
	
	public String getProductName() 
	{
		return productName;
	}
	public void setProductName(String productName) 
	{
		this.productName = productName;
	}
	public List<PackageinfoVO> getPackageList() 
	{
		return packageList.stream().collect(toList());
		
	}
	public void setPackageList(List<PackageinfoVO> packageList) 
	{
		
		this.packageList = packageList.stream().collect(toList());
	}
	
	
}
