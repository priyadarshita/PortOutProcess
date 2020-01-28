package mnp.org;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParamCheck {

	public String Check_Param(String Request_id, String Carrier_id, String MSISDN, String Cust_LastName,
			String Cust_FirtName, String DOB) {
		String Status = "SUCCESS";
		Date date = null;
		SimpleDateFormat formatter_DOB = new SimpleDateFormat("yyyy-mm-dd");

		try {
			date = formatter_DOB.parse(DOB);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}

		if (Request_id == null || Request_id.isEmpty()) {

			Status = "Request Id is empty in Request";
			System.out.println(Status);
			return Status;
		}
		if (Carrier_id == null || Carrier_id.isEmpty()) {
			Status = "Carrier id is empty in Request";
			System.out.println(Status);
			return Status;
		}
		if (Cust_LastName == null || Cust_LastName.isEmpty()) {
			Status = "Customer Last Name is empty in Request";
			System.out.println(Status);
			return Status;
		}
		if (Cust_FirtName == null || Cust_FirtName.isEmpty()) {
			Status = "Custtomer Firt Name is empty in Request";
			System.out.println(Status);
			return Status;
		}
		if (MSISDN == null || MSISDN.isEmpty()) {
			Status = "MSISDN is empty in Request";
			System.out.println(Status);
			return Status;
		}
		if (DOB == null || DOB.isEmpty()) {
			Status = "DOB is empty in Request";
			System.out.println(Status);
			return Status;
		}

		System.out.println("Request_id is : " + Request_id);
		System.out.println("Carrier_id of customer is : " + Carrier_id);
		System.out.println("MSISDN is : " + MSISDN);
		System.out.println("Customer Last Name is : " + Cust_LastName);
		System.out.println("Customer First Name is : " + Cust_FirtName);
		System.out.println("Customer date of birth is : " + formatter_DOB.format(date));
		return Status;
	}

}
