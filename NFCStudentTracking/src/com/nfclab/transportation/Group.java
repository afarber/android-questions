package com.nfclab.transportation;

import java.util.ArrayList;

public class Group {

	private static ArrayList<Student> students = new ArrayList<Student>();

	public static void process( String id, String name, String time )
	{
		boolean exist = false;
		for ( int i = 0; i < students.size(); i ++)
		{
			if ( students.get(i).getId().equals(id) ) {	
				exist = true;
				students.set(i, new Student( students.get(i).getId(),
											 students.get(i).getName(),
											 students.get(i).getEnter(),
						                     time ) );
				
			}
		}
		if ( exist == false )
		{
			students.add( new Student( id, name, time, "00:00"));
		}
	}			

	public static int size() {
		return( students.size() );
	}

	
	public static void empty()
	{
		students.clear();
	}

	public static String getStudentAsString( int i )
	{
		return( Student.getStudentAsString( students.get(i)));
	}
	
	public static String getStudentAsString( Student student )
	{
		return( student.getStudentAsString() );
				
	}

}