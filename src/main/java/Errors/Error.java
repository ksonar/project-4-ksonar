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
	public static String CAST = "Cast exception : ";
	public static String EMPTY_PURCHASE = "One of [userid, eventid, tickets] not present or empty";
	public static String NOT_MATCHING = "One of [userid, eventid, tickets] not present or empty";

}
