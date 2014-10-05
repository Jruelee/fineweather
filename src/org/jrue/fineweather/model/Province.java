/*/////////////////////////////////////////////////////////////////
                          _ooOoo_                              
                         o8888888o                             
                         88" . "88                             
                         (| ^_^ |)                             
                         O\  =  /O                             
                      ____/`---'\____                           
                    .'  \\|     |//  `.                         
                   /  \\|||  :  |||//  \                       
                  /  _||||| -:- |||||-  \                      
                  |   | \\\  -  /// |   |                      
                  | \_|  ''\---/''  |   |                      
                  \  .-\__  `-`  ___/-. /                       
                ___`. .'  /--.--\  `. . ___                     
              ."" '<  `.___\_<|>_/___.'  >'"".               
            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 
            \  \ `-.   \_ __\ /__ _/   .-` /  /                
      ========`-.____`-.___\_____/___.-`____.-'========         
                           `=---='                              
      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^       
                     ·ð×æ±£ÓÓ    ÓÀÎÞBUG                        
                       @author jrue                     
////////////////////////////////////////////////////////////////*/
package org.jrue.fineweather.model;

/**
 * @author jruelee
 *
 */
public class Province {
	private int id;
	private String provinceName;
	private String provinceCode;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	
	
}
