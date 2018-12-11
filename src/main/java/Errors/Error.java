package Errors;
/*
 * Store all errors as static for easy and clean access
 * @author ksonar
 */
public class Error {
	public static String UID = "Invalid userid : ";
	public static String PURCHASE = "Cannot purchase tickets for eventID : ";
	public static String INSERT = "Could not insert :";
	public static String UPDATE = "Could not update :";
	public static String EMPTY = "Invalid input(empty)";
	public static String CAST = "String to integer cast problem for : ";
	public static String EMPTY_PURCHASE = "One of [userid, eventid, tickets] not present or empty";
	public static String NOT_MATCHING = "One of [userid, eventid, tickets] not present or empty";
	public static String TICKETS = "Could not update tickets table";
	public static String PATH = "Invalid path, please check again";
	public static String TRANSFER = "Cannot transfer tickets, too few available, or negative tickets";

}
