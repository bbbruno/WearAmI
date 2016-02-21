import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MyLog
{
	//private static MyLog instance;
	
	private MyLog(){}
	
	/*private static MyLog getInstance()
	{
		if(instance==null)
			instance=new MyLog();
		return instance;
	}*/
	
	public static void scrivi(String fn, String data)
	{
		BufferedWriter bufferedWriter = null;
		try
		{
			// Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(fn, true));
			// Start writing to the output stream
			bufferedWriter.write(data);
			bufferedWriter.close();
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void scriviln(String fn, String data)
	{
		scrivi(fn,data+"\n");
	}
}
