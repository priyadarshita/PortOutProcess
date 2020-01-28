package mnp.org;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Order_Creation {

	int Order_id = 0, Result;
	String Status = "", Cust_Id, sql;

	public String Creation_Order(String Request_id, String Carrier_id, String MSISDN, String Cust_LastName,
			String Cust_FirtName, String DOB) throws SQLException {

		JDBCConnection db = new JDBCConnection();
		Connection connection = db.Connection_DB();

		//Date current_date = null;

		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		//String s1 = LocalDate.now().format(formatter);



		//java.sql.Date sqlDate_current = new java.sql.Date(current_date.getTime());

		DateTimeFormatter formatter_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		String timestamp = LocalDateTime.now().format(formatter_time);

		System.out.println("timestamp is " + timestamp);

		Timestamp ts = null;
		ts = Timestamp.valueOf(timestamp);

		System.out.println("sqlDate_timestamp is " + ts);

		Validation_Check vc = new Validation_Check();
		Cust_Id = vc.Customer_Id(connection, Cust_FirtName, Cust_LastName);


		// Updation in tables

		String order_update = "update `customer-order`.order set UPDATE_DATE=? , ORDER_STATUS='SUBMITTED' where CUSTID=? and  MSISDN=? ";
		PreparedStatement ps = connection.prepareStatement(order_update);

		ps.setTimestamp(1, ts);
		ps.setString(2, Cust_Id);
		ps.setString(3, MSISDN);

		int Result = ps.executeUpdate();
		if (Result == 1) {
			Status = "Order table Updation : SUCCESS";
			//System.out.println(Status);

		} else {
			Status = "Unable to update order table  ";
			System.out.println(Status);
			return Status;
		}

		String number_update = "update `customer-order`.numberinv set UPDATE_DATE=?,MSISDN_STATUS=' DISCONNECT' , STATUS_REASON=' PORTOUT' where MSISDN=?";
		ps = connection.prepareStatement(number_update);

		ps.setTimestamp(1, ts);
		ps.setString(2, MSISDN);

		Result = ps.executeUpdate();
		if (Result == 1) {
			Status = "Numberinv table Updation : SUCCESS";
			System.out.println(Status);

		} else {
			Status = "Unable to update order table";
			System.out.println(Status + "number_update");
			return Status;
		}

		String customer_update = "update `customer-order`.customer set UPDATE_DATE=?,CUST_STATUS='CANCELLED' where custid=?";
		ps = connection.prepareStatement(customer_update);

		ps.setTimestamp(1, ts);
		ps.setString(2, Cust_Id);

		Result = ps.executeUpdate();
		if (Result == 1) {
			Status = "Customer table Updation : SUCCESS";
			System.out.println(Status);

		} else {
			Status = "Unable to update order table ";
			System.out.println(Status + "customer_update");
			return Status;
		}

		return Status;

	}

}
