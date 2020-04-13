
import java.io.*;
import java.util.*;
public class IOSupport {
  private static String[] EXTRA = null;
	private static OutputStream IO_OUT = null;
	private static InputStream IO_IN = null;
	private static FileOutputStream F_OUT = null;
	private static FileInputStream F_IN = null;
	//private static DataParser DP = null;
	private static String FILE_NAME = "";
	private static String TARGET_DIR = "";
	private static Byte[] FILE_BYTES = null;
	private static long FILE_SIZE = 0;
	private static int read;
	public IOSupport(String FILE_NAME) {
		this.FILE_NAME = FILE_NAME;
		this.TARGET_DIR = FILE_NAME;
	}
	
	public IOSupport(InputStream io_in) {
		this.IO_IN = io_in;
	}
	
	public IOSupport(OutputStream io_out) {
		this.IO_OUT = io_out;
	}
	
	public IOSupport() {
		
	}
	
	public void writeMsg(String STR){
		  try{
		     new DataOutputStream(IO_OUT).write(STR.getBytes());
		  }catch(Exception e){
		     e.printStackTrace();
		  }
    }
	
	public void writeMsg(OutputStream OS,String MSG) {
		try{
		     new DataOutputStream(OS).write(MSG.getBytes());
	    }catch(Exception e){
		     e.printStackTrace();
		}
	}
	
	public String readMsg() {
		String def_ret = "";
		int c;
		try {
		while((c =IO_IN.read()) != -1 ) {
			def_ret = def_ret + Character.toString((char)c);
		}
		}catch(Exception e) {
			e.printStackTrace();
			def_ret = "";
		}
		return def_ret;
	}
	
	public int writeFile(String DIR,String CONTENT,boolean MODE) {
		int def_ret = 0;
		try {
			F_OUT = new FileOutputStream(DIR,MODE);
			F_OUT.write(CONTENT.getBytes());
			F_OUT.close();
		}catch(Exception e) {
			e.printStackTrace();
			def_ret = 0;
		}
		return def_ret;
	}
	
	public String readStr(String DIR) {
		String def_ret = "";
		try {
			F_IN = new FileInputStream(DIR);
			int c;
			while((c = F_IN.read()) != -1) {
				def_ret = def_ret + Character.toString((char)c);
			}
		}catch(Exception e) {
			e.printStackTrace();
			def_ret = "";
		}
		return def_ret;
	}
	
	public int writeStr(String path, String data, boolean state) {
		try {
			F_OUT = new FileOutputStream(path);
			F_OUT.write(data.getBytes());
			F_OUT.close();
			return 1;
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}