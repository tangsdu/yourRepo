package com.cignacmb.iuss.web.common.util;

import java.io.File;
import com.cignacmb.iuss.web.common.util.DateUtil;

/**
 * @author s4tian
 * @date 2018年8月15日 上午11:26:37
 * 
 */
public class FileDirUtils {
	
	private FileDirUtils(){}
	
	
	/**
	 * 获取文件目录
	 * @param rootDir
	 * @param source
	 * @param barCode
	 * @return
	 */
	public static String getFileDir(String rootDir,String source,String barCode,String thirdPartyOrder){
		String currentDate = DateUtil.formatCurrrentDate();
		return rootDir+File.separator+source+File.separator+currentDate+File.separator+thirdPartyOrder+File.separator+barCode+File.separator;
	}


	/**
	 * 获取文件目录
	 * @param rootDir
	 * @param source
	 * @param policyNo
	 * @return
	 */
	public static String getFileDir(String rootDir,String source){
		String currentDate = DateUtil.formatCurrrentDate();
		return rootDir+source+File.separator+currentDate+File.separator;
	}

	/**
	 * 
	 * @param flowId
	 * @param type 类型如 投保单
	 * @param status 状态如签名前，签名后
	 * @param suffix后缀如：.pdf
	 * @return
	 */
	public static String getFileName(String flowId,String type,String status,String suffix){
		return flowId + "_" + type + "_" + status + "_" + suffix;
	}

	/**
	* @Description:
	* @Param: [type,
	 * status,
	 * suffix]
	* @return: java.lang.String
	*/
	public static String getFileName(String type,String status,String suffix){
		return type + "_" + status + "_" + suffix;
	}
	
	/**
	 * 
	 * @param flowId
	 * @param type 类型如 投保单
	 * @param status 状态如签名前，签名后
	 * @param suffix后缀如：.pdf
	 * @param time 时间戳
	 * @return
	 */
	public static String getFileName(String flowId,String type,String status,String time,String suffix){
		return flowId + "_" + type + "_" + status + "_" + time + "_" + suffix ;
	}
}
