package taskManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.mindrot.BCrypt;

public class SQLQueryBuilder {
	
	private int projectNum;
	private String name;
	private Date dateDue;
	private int parentID;
	private String description;
	private String notes;
	private String assignedUserName;
	private String percentComplete;
    private int isComplete;
    private int taskIDNum;
    private int priority;
	private ArrayList<Task> tasks = new ArrayList<>();
	private ArrayList<String> users = new ArrayList<>();
	
	
	private String messageReceiver;
	private String message;
	private String messageSender;
	private ArrayList<Message> messages = new ArrayList<>();
	
	/**
	 * 
	 * Creates an empty SQLQueryBuilder object
	 * 
	 */
	public SQLQueryBuilder()
	{
	}
	
	/**
	 * 
	 * Creates a SQLQueryBuilder object with the values of the Task object to be added to the database
	 * 
	 * @param task Task object to be added to the database
	 */
	public SQLQueryBuilder(Task task)
	{
		this.taskIDNum = task.getTaskID();
		this.projectNum = Integer.parseInt(task.getProjectNum());
		this.name = task.getName();
		this.parentID = task.getParentID();
		this.dateDue = task.getDateDue();
		this.description = task.getDescription();
		this.notes = task.getNotes();
		this.percentComplete = task.getPercentComplete();
		this.assignedUserName = task.getAssignedUserName();
		this.priority = task.getPriority();
		if(task.isComplete())
		{
			this.isComplete = 1;
		}
		else
		{
			this.isComplete = 0;
		}
	}
	
	/**
	 * Adds the values of the task stored in the SQLQueryBuilder instance to the database
	 */
	void addUser(String user, String password, String first, String last, boolean admin)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			
			String query = "INSERT INTO USER VALUES(DEFAULT, ?, ?, ?, ?, ?, 1, 4)";
			
			PreparedStatement s = connection.prepareStatement(query);
			
			s.setString(1, user);
			s.setString(2,  password);
			s.setString(3,  first);
			s.setString(4,  last);
			if(admin)
			{
				s.setInt(5,  1);
			}
			else
			{
				s.setInt(5,  0);
			}
			s.execute();
			
			connection.close();
		}
		catch (SQLException e1) {
		    throw new IllegalStateException("Cannot connect the database!", e1);
	  
	  }
	}
	
	/**
	 * Adds the values of the task stored in the SQLQueryBuilder instance to the database
	 */
	void addTask(int ID, boolean isNew)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			
			String query = "INSERT INTO TASK VALUES(DEFAULT,?, ?, ?, ?, ?, ?, ?, ?,0,?,0,?,DEFAULT,DEFAULT, ?)";
			
			PreparedStatement s = connection.prepareStatement(query);
			s.setInt(1, parentID);
			s.setInt(2, ID);
			s.setInt(3, getIDFromUserName(assignedUserName));
			s.setInt(4, projectNum);
			s.setString(5, name);
			s.setDate(6, dateDue);
			s.setString(7, description);
			s.setString(8, notes);
			if(isNew)
			{
				s.setInt(9, 1);
			}
			else
			{
				s.setInt(9, 0);
			}
			s.setString(10, percentComplete);
			s.setInt(11, priority);
			s.execute();
			
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	
	//edits values of the task
	void editTask(int taskIDNum)
	{
		int assignedID = getIDFromUserName(assignedUserName);
		try(Connection connection = ConnectionPool.getConnection())
		{

			String query = "UPDATE senior.TASK SET user_assigned_ID = ?, project_num = ?, task_name = ?,  due_date = ?, task_descr = ?, "
					+ "task_notes = ?, percent_complete = ?, is_complete = ?, priority = ? WHERE task_ID = ?;";
            
			
			
			PreparedStatement s = connection.prepareStatement(query);
			s.setInt(1, assignedID);
			s.setInt(2, projectNum);
			s.setString(3, name);
			s.setDate(4, dateDue);
			s.setString(5,  description);
			s.setString(6, notes);
			s.setString(7,  percentComplete);
			s.setInt(8, isComplete);
			s.setInt(9, priority);
			s.setInt(10,  taskIDNum);
			s.execute();

			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	
	//edits values of the task
	void taskAccepted(int taskIDNum)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{

			String query = "UPDATE senior.TASK SET is_new = ? WHERE task_ID = ?;";
            
			
			
			PreparedStatement s = connection.prepareStatement(query);
			s.setInt(1, 0);
			s.setInt(2, taskIDNum);
			s.execute();

			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	
	//edits is_trash value of task when task has been deleted from a table
		void putInTrash(int taskIDNum)
		{
			try(Connection connection = ConnectionPool.getConnection())
			{

				String query = "UPDATE senior.TASK SET is_trash = ? WHERE task_ID = ?;";
	            
				
				
				PreparedStatement s = connection.prepareStatement(query);
				s.setInt(1, 1);
				s.setInt(2, taskIDNum);
				s.execute();

				connection.close();
			}
			catch (Exception e)
		    {
		      System.err.println("Got an exception!");
		      System.err.println(e.getMessage());
		    }
		}
		
		//deletes tasks from the trash table
		void deleteFromTrash(int taskIDNum)
		{
			try(Connection connection = ConnectionPool.getConnection())
			{

				String query = "DELETE FROM senior.TASK WHERE task_ID = ?;";
			            
						
						
				PreparedStatement s = connection.prepareStatement(query);
				s.setInt(1, taskIDNum);
				s.execute();

				connection.close();
			}
			catch (Exception e)
			{
				System.err.println("Got an exception!");
				System.err.println(e.getMessage());
			}
		}
		
		//used to retrieve a task from the trash table
		void retrieveFromTrash(int taskIDNum)
		{
			try(Connection connection = ConnectionPool.getConnection())
			{

				String query = "UPDATE senior.TASK SET is_trash = ? WHERE task_ID = ?;";
	            
				
				
				PreparedStatement s = connection.prepareStatement(query);
				s.setInt(1, 0);
				s.setInt(2, taskIDNum);
				s.execute();

				connection.close();
			}
			catch (Exception e)
		    {
		      System.err.println("Got an exception!");
		      System.err.println(e.getMessage());
		    }
		}
	
	/**
	 * 
	 * Catch-all function for pulling lists of tasks from the database. 
	 * 
	 * @param ID The assigned ID of the user that is requesting tasks from the database
	 * @param table The table that is being updated. Current options:
	 * 				user - Updates the table of tasks assigned to the logged in user
	 * 				all - Updates the table of all tasks in the database
	 * 				inbox - Updates the table of tasks newly assigned to the logged in user
	 * 				archive - Updates the table of tasks assigned to the logged in user that have been marked as complete
	 * @return An ArrayList of Task objects, containing all the tasks that are assigned to the logged in user
	 */
	ArrayList<Task> getTasks(int ID, String table, String search)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = null;
			
			// Determine what subset of tasks are being requested, and set query accordingly
			if(table.equals("user"))
			{
				query = "SELECT * FROM TASK WHERE user_assigned_ID = " + ID  + " AND is_complete = 0" + " AND is_new = 0" + " AND is_trash = 0";
			}
			else if(table.equals("all"))
			{
				query = "SELECT * FROM TASK"  + " WHERE is_complete = 0" + " AND is_new = 0" + " AND is_trash = 0";
			}
			else if(table.equals("inboxTasks"))
			{
				query = "SELECT * FROM TASK WHERE user_assigned_ID = '" + ID + "' AND is_new = 1" + " AND is_complete = 0" + " AND is_trash = 0";
			}
			else if(table.equals("sentTasks"))
			{
				query = "SELECT * FROM TASK WHERE user_created_ID = '" + ID + "' AND is_complete = 0" + " AND is_trash = 0";
			}
			else if(table.equals("archive"))
			{
				query = "SELECT * FROM TASK WHERE user_assigned_ID = '" + ID + "' AND is_complete = 1" + " AND is_new = 0" + " AND is_trash = 0";
			}
			else if(table.equals("allArchive"))
			{
				query = "SELECT * FROM TASK WHERE is_complete = 1" + " AND is_new = 0" + " AND is_trash = 0";
			}
			else if(table.equals("trashReceivedTasks"))
			{
				query = "SELECT * FROM TASK WHERE user_assigned_ID = '" + ID + "' AND is_trash = 1" + " AND is_new = 0";
			}
			else if(table.equals("trashSentTasks"))
			{
				query = "SELECT * FROM TASK WHERE user_created_ID = '" + ID + "' AND is_trash = 1" + " AND is_new = 0";
			}
			if(!(search.equals("")))
			{
				System.out.println("Searching...:" + search);
				query += " AND ((task_name LIKE '%"+search+"%') OR (due_date LIKE '%"+search+"%') OR (task_descr LIKE '%"+search+"%') OR (task_notes LIKE '%"+search+"%'))";
			}
			
			PreparedStatement s = connection.prepareStatement(query);
			ResultSet srs = s.executeQuery(query);
			// Loop through the result set, storing each field in a task object, then add that object to an ArrayList
			while (srs.next()) {
				{
					Task task = new Task();
					task.setProjectNum(((Integer)(srs.getInt("project_num"))).toString());
					task.setTaskID(srs.getInt("task_ID"));
					task.setParentID(srs.getInt("parent_ID"));
					task.setName(srs.getString("task_name"));
					task.setDateDue(srs.getDate("due_date"));
					task.setAssignedUserID(srs.getInt("user_assigned_ID"));
					task.setAssignedUserName(getUserNameFromID(srs.getInt("user_assigned_ID")));
					task.setDescription(srs.getString("task_descr"));
					task.setNotes(srs.getString("task_notes"));
					task.setPercentComplete(srs.getString("percent_complete"));
					task.setComplete(srs.getBoolean(("is_complete")));
					task.setIsNew(srs.getBoolean("is_new"));
					task.setDateCreated(srs.getTimestamp("date_created"));
					task.setLastModified(srs.getTimestamp("last_modified"));
					task.setPriority(srs.getInt("priority"));
					tasks.add(task);
				}
			}
			
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		return tasks;
	}
	
	ArrayList<Task> getSubTasks(int taskID)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = "SELECT * FROM TASK WHERE parent_ID = " + taskID + " AND is_trash = 0";
			
			PreparedStatement s = connection.prepareStatement(query);
			ResultSet srs = s.executeQuery(query);
			// Loop through the result set, storing each field in a task object, then add that object to an ArrayList
			while (srs.next()) {
				{
					Task task = new Task();
					task.setProjectNum(((Integer)(srs.getInt("project_num"))).toString());
					task.setTaskID(srs.getInt("task_ID"));
					task.setParentID(srs.getInt("parent_ID"));
					task.setName(srs.getString("task_name"));
					task.setDateDue(srs.getDate("due_date"));
					task.setAssignedUserID(srs.getInt("user_assigned_ID"));
					task.setAssignedUserName(getUserNameFromID(srs.getInt("user_assigned_ID")));
					task.setDescription(srs.getString("task_descr"));
					task.setNotes(srs.getString("task_notes"));
					task.setPercentComplete(srs.getString("percent_complete"));
					task.setComplete(srs.getBoolean(("is_complete")));
					task.setIsNew(srs.getBoolean("is_new"));
					task.setDateCreated(srs.getTimestamp("date_created"));
					task.setLastModified(srs.getTimestamp("last_modified"));
					task.setPriority(srs.getInt("priority"));
					tasks.add(task);
					System.out.println(task.toString());
				}
			}
			
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		return tasks;
	}
	
	void newMessage(int receiverID, String message, int senderID)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			
			String query = "INSERT INTO MESSAGE VALUES(DEFAULT, ?, ?, ?, 0, 0, 0, 0)";
			
			PreparedStatement s = connection.prepareStatement(query);
			
			s.setInt(1, receiverID);
			s.setString(2, message);
			s.setInt(3, senderID);
			s.execute();
			
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	
	/**
	 * 
	 * Catch-all function for pulling lists of tasks from the database. 
	 * 
	 * @param ID The assigned ID of the user that is requesting tasks from the database
	 * @param table The table that is being updated. Current options:
	 * 				inboxMessages - Updates the table of messages sent to the logged in user
	 * 				sentMessages - Updates the table of messagess sent by the logged in user
	 * @return An ArrayList of Message objects, containing all the messages that are correspond to the logged in user
	 */
	ArrayList<Message> getMessages(int ID, String table)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = null;
			
			// Determine what subset of messages are being requested, and set query accordingly
			if(table.equals("inboxMessages"))
			{
				query = "SELECT * FROM MESSAGE WHERE user_received_ID = '" + ID + "' AND user_received_is_trash = 0" + " AND user_received_remove_trash = 0";
			}
			else if(table.equals("sentMessages"))
			{
				query = "SELECT * FROM MESSAGE WHERE user_created_ID = '" + ID + "' AND user_created_is_trash = 0" + " AND user_created_remove_trash = 0";
			}
			else if(table.equals("inboxMessagesTrash"))
			{
				query = "SELECT * FROM MESSAGE WHERE user_received_ID = '" + ID + "' AND user_received_is_trash = 1" + " AND user_received_remove_trash = 0";
			}
			else if(table.equals("sentMessagesTrash"))
			{
				query = "SELECT * FROM MESSAGE WHERE user_created_ID = '" + ID + "' AND user_created_is_trash = 1" + " AND user_created_remove_trash = 0";
			}
			PreparedStatement s = connection.prepareStatement(query);
			ResultSet srs = s.executeQuery(query);
			// Loop through the result set, storing each field in a message object, then add that object to an ArrayList
			while (srs.next()) {
				{
					Message message = new Message();
					int receiverID = srs.getInt("user_received_ID");
					int senderID = srs.getInt("user_created_ID");
					message.setReceiver(new SQLQueryBuilder().getUserNameFromID(receiverID));
					message.setSender(new SQLQueryBuilder().getUserNameFromID(senderID));
					message.setMessage(srs.getString("message"));
					message.setMessageID(srs.getInt("message_ID"));
					messages.add(message);
				}
			}
			
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		return messages;
	}
	
	void putMessageInTrash(int messageID, String table)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = null;
			
			// Determine what subset of messages are being requested, and set query accordingly
			if(table.equals("inboxMessages"))
			{
				query = "UPDATE senior.MESSAGE SET user_received_is_trash = 1 WHERE message_ID = " + messageID;
			}
			else if(table.equals("sentMessages"))
			{
				query = "UPDATE senior.MESSAGE SET user_created_is_trash = 1 WHERE message_ID = " + messageID;
			}
			PreparedStatement s = connection.prepareStatement(query);
			s.execute();
			
			
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	
	void removeMessageFromTrash(int messageID, String table)
	{
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = null;
			
			// Determine what subset of messages are being requested, and set query accordingly
			if(table.equals("inboxMessages"))
			{
				query = "UPDATE senior.MESSAGE SET user_received_remove_trash = 1 WHERE message_ID = " + messageID;
			}
			else if(table.equals("sentMessages"))
			{
				query = "UPDATE senior.MESSAGE SET user_created_remove_trash = 1 WHERE message_ID = " + messageID;
			}
			PreparedStatement s = connection.prepareStatement(query);
			s.execute();
			
			query = "DELETE FROM MESSAGE WHERE message_ID = '" + messageID + "' AND user_received_is_trash = 1" + " AND user_received_remove_trash = 1" + "  AND user_created_is_trash = 1" + " AND user_created_remove_trash = 1";
			
			PreparedStatement s2 = connection.prepareStatement(query);
			s2.execute();
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
	}
	
	ArrayList<String> getUsers()
	 	{
	 		try(Connection connection = ConnectionPool.getConnection()) {
	 			String query = "SELECT * FROM USER";
	 			
	 			PreparedStatement s = connection.prepareStatement(query);
	 			ResultSet srs = s.executeQuery();
	 			while(srs.next())
	 			{
	 				users.add(srs.getString("username"));
	 			}
	 			connection.close();
	 			return users;
	 			
	 		} catch (SQLException e1) {
	 		    throw new IllegalStateException("Cannot connect to the database!", e1);
	 		    } 
	 	}
	
	int getUserCreatedID(int taskID)
	{
		int userCreated = 0;
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = "SELECT * FROM TASK WHERE task_ID = " + taskID;
			
			PreparedStatement s = connection.prepareStatement(query);
			ResultSet srs = s.executeQuery(query);
			if(srs.next()) {
				userCreated = srs.getInt("user_created_ID");
			}
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		return userCreated;
	}
	
	/**
	 * Converts a user ID number into the corresponding username
	 * 
	 * @param ID The ID number of the username to be looked up
	 * @return The username corresponding to the given ID number
	 */
	String getUserNameFromID(int ID)
	{
		String nameOfUser = null;
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = "SELECT * FROM USER WHERE user_ID = " + ID;
			
			PreparedStatement s = connection.prepareStatement(query);
			ResultSet srs = s.executeQuery(query);
			
			if(srs.next()) {
				nameOfUser = srs.getString("username");
			}
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		return nameOfUser;
	}

	/**
	 * Converts a username into the corresponding user ID
	 * 
	 * @param nameOfUser The username of the user ID to be looked up
	 * @return The user ID corresponding to the given username
	 */
	int getIDFromUserName(String nameOfUser)
	{
		int ID = 0;
		try(Connection connection = ConnectionPool.getConnection())
		{
			String query = "SELECT * FROM USER";
			
			PreparedStatement s = connection.prepareStatement(query);
			ResultSet srs = s.executeQuery(query);
			
			while(srs.next()) {
				if(srs.getString("username").equals(nameOfUser))
				{
					ID = srs.getInt("user_ID");
				}
			}
			connection.close();
		}
		catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		return ID;
	}
	
	/**
	 * 
	 * Checks the given password against the salted hash in the database
	 * 
	 * @param nameOfUser The username typed in by the user
	 * @param passwordOfUser The password typed in by the user
	 * @return An ArrayList of Task objects, containing all the tasks that are assigned to the logged in user
	 */
	boolean checkPassword(String nameOfUser, char[] passwordOfUser)
	{
		try(Connection connection = ConnectionPool.getConnection()) {
			String query = "SELECT * FROM USER WHERE username = ?";
			
			PreparedStatement s = connection.prepareStatement(query);
			s.setString(1, nameOfUser);
			ResultSet srs = s.executeQuery();
			srs.next();
			String hashed = srs.getString("password");
			
			return BCrypt.checkpw(String.valueOf(passwordOfUser), hashed);
			
		} catch (SQLException e1) {
		    throw new IllegalStateException("Cannot connect to the database!", e1);
		    } 
	}
}