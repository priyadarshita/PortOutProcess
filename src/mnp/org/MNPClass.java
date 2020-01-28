package mnp.org;

import java.sql.Connection;
import java.sql.SQLException;

public class MNPClass implements MNPInterface {

	@Override
	public String operations(String Request_id, String Carrier_id, String MSISDN, String Cust_LastName,
			String Cust_FirtName, String DOB) throws SQLException {

		String Status = "";
		Connection con;
		
		//Parmaters Check

		ParamCheck obj1 = new ParamCheck();
		Status = obj1.Check_Param(Request_id, Carrier_id, MSISDN, Cust_LastName, Cust_FirtName, DOB);

		if (Status != "SUCCESS") {
			System.out.println("Error is : " + Status);
			return Status;
		}
		
		//Validations Check

		Validation_Check vc = new Validation_Check();
		Status = vc.Check_Valid(Request_id, Carrier_id, MSISDN, Cust_LastName, Cust_FirtName, DOB);

		if (Status.contains("Failed")) {
			System.out.println("Error is : " + Status);
			return Status;
		}
		
		//Back Office Work

		BackOffice_Check boc = new BackOffice_Check();
		Status = boc.Check_BackOffice(Request_id, Carrier_id, MSISDN, Cust_LastName, Cust_FirtName, DOB);

		if (Status == "Back Office task completed") {
			Order_Creation oc = new Order_Creation();
			Status = oc.Creation_Order(Request_id, Carrier_id, MSISDN, Cust_LastName, Cust_FirtName, DOB);

			System.out.println("Status from Order Creation : " + Status);

			if (Status.contains("SUCCESS")) {

				Status = "Port Out has been successfully completed";
				System.out.println(Status);
				
			}

			return Status;

		} else {
			JDBCConnection db = new JDBCConnection();
			con = db.Connection_DB();
			con.close();

			return Status;
		}

	}

}
