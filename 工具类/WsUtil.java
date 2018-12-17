package com.cignacmb.esales.utils;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;


/**
 * 通用WebService工具管理类
 * @author Jeck
 * @createdate 2016年3月24日 上午11:57:48
 */
public class WsUtil {
	
	/**
	 * 服务调用URL(映射)
	 */
	private String wsdlURL;
	
	/**
	 * 服务命名空间URI(映射服务端服务类的包名)
	 */
	private String nameSpaceURI;
	
	/**
	 * 服务方法名(映射服务端服务类的方法)
	 */
	private String operationName;
	
	public WsUtil(String wsdlURL, String nameSpaceURI, String operationName) {
		this.wsdlURL = wsdlURL;
		this.nameSpaceURI = nameSpaceURI;
		this.operationName = operationName;
	}
	
	/**
	 * 调用WebService服务
	 * @param paramNames 参数名数组，
	 * @param paramValues 参数值数组，必须与参数名顺序一致
	 * @return 服务端方法返回值
	 * @throws ServiceException 
	 * @throws RemoteException 
	 * @creator Jeck
	 * @createdate 2016年3月24日 下午2:10:21
	 */
	public Object invokeService(String[] paramNames, Object... paramValues) throws ServiceException, RemoteException   {
		Service service = new Service();
		Call client = (Call)service.createCall();
		client.setTargetEndpointAddress(wsdlURL);
		//设置方法名，包名+方法名。 包名命名以服务端为主
		client.setOperationName(new QName(nameSpaceURI, operationName));
		//SOAP启动服务 调用并接受返回数据。false为仅调用，没有返回数据。
		client.setUseSOAPAction(true);
		//服务端方法参数名添加
		for(String paramName : paramNames){
			client.addParameter(paramName,XMLType.XSD_STRING,ParameterMode.IN);
			client.setReturnType(XMLType.XSD_STRING);
		}
		return client.invoke(paramValues);
	}
	
	public String getWsdlURL() {
		return wsdlURL;
	}

	public void setWsdlURL(String wsdlURL) {
		this.wsdlURL = wsdlURL;
	}

	public String getNameSpaceURI() {
		return nameSpaceURI;
	}

	public void setNameSpaceURI(String nameSpaceURI) {
		this.nameSpaceURI = nameSpaceURI;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	
	public static void main(String[] args) throws Exception {
		
	    String wsdlURL = "http://ip:18090/iuss-service-insuring/services/notice?wsdl";
	    String nameSpaceURI ="http://webservice.esales.cignacmb.com";
	    String operationName ="artificialUnderwriting";
	    WsUtil client = new WsUtil(wsdlURL, nameSpaceURI, operationName);
	    String[] paramNames = {"insuranceReq"};
	    String str = "<?xml version=\"1.0\" encoding=\"GBK\"?>";
	    str+="<TransData><BaseInfo><TransType>01</TransType><TransCode>010001</TransCode><TransDate>2018-10-23</TransDate><TransTime>11:30:27</TransTime><TransSeq>03001810100990065183</TransSeq><TransSourceSys>0300</TransSourceSys><SysOperator>SYS</SysOperator></BaseInfo><InputData><ContInfo><Cont><UWFlag>4</UWFlag><Position>2243</Position><ContNo>P00001261797</ContNo><PrtNo>423442</PrtNo></Cont></ContInfo></InputData></TransData>";
	    client.invokeService(paramNames, str);

	}
	
}
