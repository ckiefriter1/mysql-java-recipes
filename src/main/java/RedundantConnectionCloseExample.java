import java.sql.*;

public class RedundantConnectionCloseExample {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean error = false;

	try {
	  conn = getConnection();
	  startTransaction();
	  
	  try {
	    Statement stmt = conn.createStatement(sql);
	    ResultSet rs = stmt.executeQuery();

	    while(rs.next()) {
	      // ...
	    }

	    commitTransaction();
	  }
	  catch(Exception e) {
	    rollbackTransaction();
	    error = true;
	  }
	}
	catch(SQLException e) {
	  error = true;
	}
	finally {
	  if(rs != null) {
	    try {
	      rs.close();
	    }
	    catch(Exception e) {
	      error = true;
	    }
	  }

	  if(stmt != null) {
	    try {
	      stmt.close();
	    }
	    catch(Exception e) {
	      error = true;
	    }
	  }

	  if(conn != null) {
	    try {
	      conn.close();
	    }
	    catch(Exception e) {
	      error = true;
	    }
	  }
	}

	if(error) {
	  throw new RuntimeException("Resources may have leaked!");
	}
}
