package com.nfclab.transportation;

public class Student {

	private String id;
	private String name;
	private String enter;
	private String exit;
	
	public Student ( String id, String name, String enter, String exit )
	{
		this.id = id;
		this.name = name;
		this.enter = enter;
		this.exit = exit;
	}
	
	public Student getStudent()
	{
		return( this );
	}

	public String getId() {
		return( this.id );
	}
	
	public String getName() {
		return( this.name );
	}
	
	public String getEnter() {
		return( this.enter );
	}
	
	public String getExit() {
		return( this.exit );
	}
	
	public String getStudentAsString()
	{
		return( this.id + " \t" +
				this.name + " \t" +
				this.enter + " \t" +
				this.exit + " \n" );
	}
	
	public static String getStudentAsString( Student student )
	{
		return( student.id + " \t" +
				student.name + " \t" +
				student.enter + " \t" +
				student.exit + " \n" );
	}
	
}
