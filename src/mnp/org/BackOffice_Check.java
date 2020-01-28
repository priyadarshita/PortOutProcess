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

public class BackOffice_Check {

	int Task_Id = 0, check1 = 0, check2 = 0, Order_id = 0, Result;
	String Cust_Id, Status, Check, order_update, sql;

	public String Check_BackOffice(String Request_id, String Carrier_id, String MSISDN, String Cust_LastName,
			String Cust_FirtName, String DOB) throws SQLException {

		JDBCConnection db = new JDBCConnection();
		Connection connection = db.Connection_DB();

		System.out.println("Back Office Work Starts");

		Date current_date = null;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String s1 = LocalDate.now().format(formatter);

		try {
			current_date = new SimpleDateFormat("yyyy-mm-dd").parse(s1);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		java.sql.Date sqlDate_current = new java.sql.Date(current_date.getTime());
		String Task_Details = "";

		// Task_Id = Task_Id(connection);

		DateTimeFormatter formatter_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		String timestamp = LocalDateTime.now().format(formatter_time);

		Timestamp ts = null;
		ts = Timestamp.valueOf(timestamp);

		Validation_Check vc = new Validation_Check();
		Cust_Id = vc.Customer_Id(connection, Cust_FirtName, Cust_LastName);

		// To Check if there is entry in Order Table

		PreparedStatement ps = connection
				.prepareStatement("SELECT count(*) FROM `customer-order`.`order` where msisdn=? and custid=? ");
		ps.setString(1, MSISDN);
		ps.setString(2, Cust_Id);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Result = rs.getInt(1);
		}

		System.out.println("Existing entry in Order Table " + Result);

		// Feching next Order Id

		ps = connection.prepareStatement("SELECT max(order_id) FROM `customer-order`.order");
		rs = ps.executeQuery();

		while (rs.next()) {
			Order_id = rs.getInt(1);
			++Order_id;
			System.out.println("Next order_id is " + Order_id);
		}

		// Order Creation

		if (Result == 0) {
			sql = "insert into `customer-order`.order(order_ID,CREATION_DATE,UPDATE_DATE,CUSTID,MSISDN,ext_portrequest,carrier_id,order_status,STATUS_reason) values(?,?,?,?,?,?,?,?,?)";
			ps = connection.prepareStatement(sql);

			ps.setInt(1, Order_id);
			ps.setDate(2, sqlDate_current);
			ps.setTimestamp(3, ts);
			ps.setString(4, Cust_Id);
			ps.setString(5, MSISDN);
			ps.setString(6, Request_id);
			ps.setString(7, Carrier_id);
			ps.setString(8, "CREATED");
			ps.setString(9, "PORTOUT");

			Result = ps.executeUpdate();
			if (Result == 1) {
				Status = "Order Creation : SUCCESS";
				System.out.println("Order Creation : SUCCESS ");

			} else {
				Status = "Unable to update order table";
				System.out.println(Status);
				return Status;
			}
		}

		// Checking for completed status in worklist
		ps = connection.prepareStatement(
				"SELECT count(*) FROM `customer-order`.worklist where custid=? and msisdn=? and task_status='completed'");
		ps.setString(1, Cust_Id);
		ps.setString(2, MSISDN);

		rs = ps.executeQuery();

		while (rs.next()) {
			check1 = rs.getInt(1);
		}

		System.out.println("Enry in worklist table check1 " + check1);

		// Checking for created status in worklist
		ps = connection.prepareStatement(
				"SELECT count(*) FROM `customer-order`.worklist where custid=? and msisdn=? and task_status='created'");
		ps.setString(1, Cust_Id);
		ps.setString(2, MSISDN);

		rs = ps.executeQuery();

		while (rs.next()) {
			check2 = rs.getInt(1);
		}

		System.out.println("Enry in worklist table check2 " + check2);

		if (check1 == 3) {
			Status = "Back Office task completed";
			System.out.println(Status);
			return Status;
		}

		else if (check2 == 3) {
			// Manual task are yet to be performed
			order_update = "update `customer-order`.order set UPDATE_DATE=? where CUSTID=? and  MSISDN=? and EXT_PORTREQUEST=?";
			ps = connection.prepareStatement(order_update);

			ps.setTimestamp(1, ts);
			ps.setString(2, Cust_Id);
			ps.setString(3, MSISDN);
			ps.setString(4, Request_id);

			int Result = ps.executeUpdate();
			if (Result == 1) {
				Status = "Order table Updation : SUCCESS";
				// System.out.println(Status);

			} else {
				Status = "Unable to update order table  ";
				System.out.println(Status);
				return Status;
			}

			Status = "All Manual tasks are yet to be performed. Please complete all manual task";
			System.out.println(Status);
			return Status;
		}

		else if (check1 >= 1 && check2 >= 1) {

			order_update = "update `customer-order`.order set UPDATE_DATE=?  where CUSTID=? and  MSISDN=? and EXT_PORTREQUEST=?";
			ps = connection.prepareStatement(order_update);

			ps.setTimestamp(1, ts);
			ps.setString(2, Cust_Id);
			ps.setString(3, MSISDN);
			ps.setString(4, Request_id);

			int Result = ps.executeUpdate();
			if (Result == 1) {
				Status = "Order table Updation : SUCCESS";
				// System.out.println(Status);

			} else {
				Status = "Unable to update order table  ";
				System.out.println(Status);
				return Status;
			}
			Status = "Some Manual tasks are yet to be performed. Please complete the remaining manual tasks ";
			System.out.println(Status);
			return Status;
		}

		else {
			// insert into worklist table

			System.out.println("There is no entry in Worklist. So inserting tasks for worklist");

			Task_Id = Task_Id(connection);

			for (int i = 0; i <= 2; i++) {

				Task_Id = Task_Id + 1;

				sql = "insert into worklist(TASKID,CREATION_DATE,UPDATE_DATE,CUSTID,MSISDN,TASK_DETAILS,TASK_STATUS) values(?,?,?,?,?,?,?)";
				ps = connection.prepareStatement(sql);

				if (i == 0) {
					Task_Details = "Customer Confirmation";
				} else if (i == 1) {
					Task_Details = "Retension Activities";

				} else
					Task_Details = "Contract eligibility check ";

				ps.setInt(1, Task_Id);
				ps.setDate(2, sqlDate_current);
				ps.setTimestamp(3, ts);
				ps.setString(4, Cust_Id);
				ps.setString(5, MSISDN);
				ps.setString(6, Task_Details);
				ps.setString(7, "created");

				Result = ps.executeUpdate();
				if (Result == 1) {
					System.out.println("Worklist table has been successfully updated for : " + Task_Details);
					Status = "Manual tasks are in created state. Please complete the manual order first. ";

				} else {
					Status = "Unable to update worklist table: " + Task_Details;
					System.out.println(Status);
					return Status;
				} // check for 1-year contract starts

			}

			return Status;
		}

		// return Status;

	}

	public int Task_Id(Connection c) throws SQLException {
		int Task_id = 0;
		PreparedStatement ps = c.prepareStatement("select max(taskid) from `customer-order`.worklist;");

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Task_id = rs.getInt(1);
			System.out.println("Maximum task_id is " + Task_id);
		}
		return Task_id;

	}

}
