package mnp.org;

import java.sql.SQLException;

public interface MNPInterface {
	
	public  String operations(String Request_id,String Carrier_id,String MSISDN, String Cust_LastName, String Cust_FirtName, String DOB ) throws  SQLException;

}
