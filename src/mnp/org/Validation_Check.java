package mnp.org;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Validation_Check {

	int Result = 0;

	String Status, Valid_Status, Check, Cust_Id;
	Date date = null;
	java.sql.Date sqlDate = null;

	public String Check_Valid(String Request_id, String Carrier_id, String MSISDN, String Cust_LastName,
			String Cust_FirtName, String DOB) throws SQLException {
		JDBCConnection db = new JDBCConnection();
		Connection connection = db.Connection_DB();
		SimpleDateFormat formatter_DOB = new SimpleDateFormat("yyyy-MM-dd");

		try {
			date = formatter_DOB.parse(DOB);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}

		// Carrier Id Validation
		PreparedStatement ps = connection
				.prepareStatement("SELECT count(*) FROM `customer-order`.carrier where carrier_id=?");
		ps.setString(1, Carrier_id);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}
		Valid_Status = "Carrier Id Validation : ";
		Check = "Carrier Id does not exist in the System. Validation : ";
		Status = Result_check(Result, Check, Valid_Status);

		if (Status.contains("Failed")) {
			return Status;
		}

		// MSISDN validation

		ps = connection.prepareStatement("SELECT count(*) FROM `customer-order`.numberinv where msisdn=?");
		ps.setString(1, MSISDN);

		rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}
		Valid_Status = "MSISDN Validation : ";
		Check = "MSISDN does not exist in the System. Validation : ";
		Status = Result_check(Result, Check, Valid_Status);

		if (Status.contains("Failed")) {
			return Status;
		}

		// MSISDN Status

		ps = connection.prepareStatement(
				"SELECT count(*) FROM `customer-order`.numberinv where msisdn=? and msisdn_status in('active','suspended')");
		ps.setString(1, MSISDN);

		rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}
		Valid_Status = "MSISDN Status Validation : ";
		Check = "MSISDN Status is either not in active State or Suspended State in the System. Validation : ";
		Status = Result_check(Result, Check, Valid_Status);

		if (Status.contains("Failed")) {
			return Status;
		}

		// Checking for MSISDN Incollection status

		ps = connection.prepareStatement(
				"SELECT count(*) FROM `customer-order`.numberinv where msisdn=? and  msisdn_status in ('active') or (msisdn_status='suspended' and status_reason!='incollection') ");
		ps.setString(1, MSISDN);

		rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}
		Valid_Status = "MSISDN Incollection Status Validation : ";
		Check = "MSISDN is in Suspended State and the reason is Incollection Status. Validation : ";
		Status = Result_check(Result, Check, Valid_Status);

		if (Status.contains("Failed")) {
			return Status;
		}
		// Fetch current date and timestamp

		String Cust_DOB = formatter_DOB.format(date);

		SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd");

		try {
			sqlDate = new java.sql.Date(format3.parse(Cust_DOB).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ps = connection.prepareStatement(
				"SELECT count(*) FROM `customer-order`.customer where first_name=? and last_name=? and cust_dob=?");
		ps.setString(1, Cust_FirtName);
		ps.setString(2, Cust_LastName);
		ps.setDate(3, sqlDate);

		rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}

		if (Result == 0) {
			// Customer First Name Validation

			ps = connection.prepareStatement("SELECT count(*) FROM `customer-order`.customer where first_name=?");
			ps.setString(1, Cust_FirtName);

			rs = ps.executeQuery();

			while (rs.next()) {
				Result = rs.getInt(1);
			}
			Valid_Status = "Customer First Name Validation : ";
			Check = "Customer First Name is not Correct. Validation : ";
			Status = Result_check(Result, Check, Valid_Status);

			if (Status.contains("Failed")) {
				return Status;
			}

			// Customer Last Name Validation

			ps = connection.prepareStatement("SELECT count(*) FROM `customer-order`.customer where  last_name=?");
			ps.setString(1, Cust_LastName);

			rs = ps.executeQuery();

			while (rs.next()) {
				Result = rs.getInt(1);
			}
			Valid_Status = "Customer Last Name Validation : ";
			Check = "Customer Last Name is not Correct. Validation : ";
			Status = Result_check(Result, Check, Valid_Status);

			if (Status.contains("Failed")) {
				return Status;
			}

			// Customer Date Of Birth Validation

			ps = connection.prepareStatement("SELECT count(*) FROM `customer-order`.customer where  cust_dob=?");

			ps.setDate(1, sqlDate);

			System.out.println("Cust_DOB is " + sqlDate);

			rs = ps.executeQuery();

			while (rs.next()) {
				Result = rs.getInt(1);
			}
			Valid_Status = "Customer Date Of Birth Validation : ";
			Check = "Customer Date Of Birth is not Correct. Validation : ";
			Status = Result_check(Result, Check, Valid_Status);

			if (Status.contains("Failed")) {
				return Status;
			}
		}

		String Cust_Id = Customer_Id(connection, Cust_FirtName, Cust_LastName);

		// Customer Status Validation

		ps = connection.prepareStatement(
				"SELECT count(*) FROM `customer-order`.customer where custid=? and cust_status in ('active','suspended')");
		ps.setString(1, Cust_Id);

		rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}
		Valid_Status = "Customer Status Validation : ";
		Check = "Customer Status is either not in Active State Or Suspended State. Validation : ";
		Status = Result_check(Result, Check, Valid_Status);

		if (Status.contains("Failed")) {
			return Status;
		}

		// Customer Dues Check

		ps = connection
				.prepareStatement("SELECT count(*) FROM `customer-order`.customer where custid=? and cust_balance<=0");
		ps.setString(1, Cust_Id);

		rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}
		Valid_Status = "Customer Dues Validation : ";
		Check = "Customer has some dues pending. Validation : ";
		Status = Result_check(Result, Check, Valid_Status);

		if (Status.contains("Failed")) {
			return Status;
		}

		// Existing Cancel Order is Order Table

		ps = connection.prepareStatement(
				"SELECT count(*) FROM `customer-order`.`order` where msisdn=? and order_status ='CANCELLED' ");
		ps.setString(1, MSISDN);

		rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}

		if (Result == 1) {
			Result = 0;
		} else {
			Result = 1;
		}
		Valid_Status = "Cancel Order already exist : ";
		Check = "Order already exist in cancelled state. Validation :  ";
		Status = Result_check(Result, Check, Valid_Status);

		if (Status.contains("Failed")) {
			return Status;
		}

		return Status;
	}

	public String Result_check(int Result, String Check, String Valid_Status) {
		if (Result == 1) {
			Status = Valid_Status + "SUCCESS";
			System.out.println(Status);
		} else {
			Status = Check + "Failed";
			System.out.println(Check + "Failed");
			return Status;
		}
		return Status;
	}

	public String Customer_Id(Connection c, String Cust_FirtName, String Cust_LastName) throws SQLException {
		String Cust_Id = null;
		PreparedStatement psa = c
				.prepareStatement("select custid from `customer-order`.customer where first_name=? and last_name=?");
		psa.setString(1, Cust_FirtName);
		psa.setString(2, Cust_LastName);

		ResultSet rsa = psa.executeQuery();

		while (rsa.next()) {
			Cust_Id = rsa.getString(1);
			System.out.println("Customer Id is : " + Cust_Id);
		}
		return Cust_Id;

	}

}
