

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ArinaMainBackend
 */
@WebServlet("/ArinaMainBackend")
public class ArinaMainBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ROOT = "/home/technet/Arina";
    private static final String PROF_DIR = ROOT + "/Profiles/";
    private static final String REQ_DIR = ROOT + "/Requests/";
    private static final String ACTIVE_DIR = ROOT + "/Active/";
    private static final String APPR_DIR = ROOT + "/Approved/";
    private static final String CMD_DIR = ROOT + "/Command/";
    private static final String CONN_DIR = ROOT + "/Connected/";
    private static final String FORM_DIR = ROOT + "/Formats/";
    private static String COUNTRY = "";
    private static String USER_NAME = "";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ArinaMainBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	private String FROM_CLIENT;
	private static OutputStream SERVER_OUT;
	private InputStream SERVER_IN;
	private IOSupport IOS;
	private DataParser DP;
	HttpServletResponse RESPONSE;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		try {
			SERVER_IN = request.getInputStream();
			SERVER_OUT = response.getOutputStream();
			RESPONSE = response;
			IOS = new IOSupport(SERVER_IN);
			FROM_CLIENT = IOS.readMsg();
			System.out.println(FROM_CLIENT);
			DP = new DataParser(FROM_CLIENT);
			String type = "";
			if(DP.getTotalItems() == 0) {
			type = DP.getValue("DATA_TYPE");
			}else {
				type = DP.getValue(0,"DATA_TYPE");
			}
			switch(type) {
			case "PROFILE":regUser();
						   break;
			case "REQUESTS":getRequests();
						    break;
			case "REQUEST":postRequest();
			               break;
			case "REQUEST_S":postApproved();
			                 break;                 
			case "ACTIVE":getActive();
					      break;
			case "USERS":getUsers();
			             break;
			case "APPROVED":getApproved();
			                break;                
			case "INITIATE":postInitiate();
			                break;
			case "WAITING":postInitiate();
			               break;
			case "CONN_IN_NOTICE":postConnectIn();
			               break;
			case "CONN_PASS_NOTICE":postConnectPass();
			                break;
			case "COMMAND_PICK":getCommands();
			                break;
			case "COMMANDS":postCmd();
			                break;
			case "FORMATS_IN":postFormat();
			                  break;
			case "FORMATS_OUT":getFormat();
			                  break;
			default: System.out.println("UNKNOWN POST OPS CODE");               
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void regUser() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		if(!(new File(PROF_DIR + COUNTRY).exists())) {
			System.out.println("PROFE");
			new File(PROF_DIR + COUNTRY).mkdirs();
			IOS.writeStr(PROF_DIR + COUNTRY + "/Profiles.data",DP.getCleanJSON(),false);
			IOS.writeStr(PROF_DIR + COUNTRY + "/Profiles.pop","1",false);
			IOS.writeStr(PROF_DIR + COUNTRY + "/" + USER_NAME + ".data",FROM_CLIENT,false);
		}else {
			IOS.writeStr(PROF_DIR + COUNTRY + "/Profiles.data",("," + DP.getCleanJSON()),true);
			updateIndex(PROF_DIR + COUNTRY + "/Profiles.pop");
			IOS.writeStr(PROF_DIR + COUNTRY + "/" + USER_NAME + ".data",FROM_CLIENT,false);
		}
		updateProfDtb();
		IOS.writeMsg(SERVER_OUT,"DONE");
	}
	
	public void updateProfDtb() {
		if(IOS.readStr(PROF_DIR + "Profiles.pop").equals("")) {
			IOS.writeStr(PROF_DIR + "Profiles.pop","1",false);
			IOS.writeStr(PROF_DIR + "Profiles.data",DP.getCleanJSON(),false);
			IOS.writeStr(PROF_DIR + "Names.data",DP.getIndexedPair("NAME",0,USER_NAME),false);
			/*getIndexedPair returns {NAME0:$USER_NAME}*/
		}else {
			int pop = Integer.valueOf(IOS.readStr(PROF_DIR + "Profiles.pop")) + 1;
			IOS.writeStr(PROF_DIR + "Profiles.pop",String.valueOf(pop),false);
			IOS.writeStr(PROF_DIR + "Profiles.data",("," + DP.getCleanJSON()),true);
			IOS.writeStr(PROF_DIR + "Names.data",("," + DP.getIndexedPair("NAME",0,USER_NAME)),true);
		}
	}
	
	public void updateIndex(String dir) {
		int pop = Integer.valueOf(IOS.readStr(dir)) + 1;
		IOS.writeStr(dir,String.valueOf(pop),false);
	}
	
	private void getRequests() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		if(!(new File(REQ_DIR + COUNTRY + "/" + USER_NAME + ".rqs").exists())) {
			IOS.writeMsg(SERVER_OUT,"NO_REQUESTS");
		}else {
			IOS.writeMsg(SERVER_OUT,"[" + IOS.readStr(REQ_DIR + COUNTRY + "/" + USER_NAME + ".rqs") + "]");
		}
	}
	
	private void getUsers() {
		COUNTRY = DP.getValue("COUNTRY");
		if(!(new File(PROF_DIR + COUNTRY).exists())){
			IOS.writeMsg(SERVER_OUT,"NO_USERS");
		}else {
			IOS.writeMsg(SERVER_OUT,"[" + IOS.readStr(PROF_DIR + COUNTRY + "/Profiles.data") + "]");
		}
	}
	
	private void getActive() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		if(!(new File(ACTIVE_DIR + COUNTRY + "/" + USER_NAME + ".act").exists())) {
			IOS.writeMsg(SERVER_OUT,"NO_ACTIVE");
		}else {
			IOS.writeMsg(SERVER_OUT,"[" + IOS.readStr(ACTIVE_DIR + COUNTRY + "/" + USER_NAME + ".act") + "]");
		}
	}
	
	
	private void getApproved() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		if(!(new File(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr").exists())) {
			IOS.writeMsg(SERVER_OUT,"NO_CONFIRMS");
		}else {
			if(new File(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr").length() > 4) {
				String data = "[" + IOS.readStr(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr") + "]";
				DP = new DataParser(data);
				data = DP.removeObjs("DATA_TYPE","REQUEST_S");
				//System.out.println("rem" + "----" + data);
				IOS.writeMsg(SERVER_OUT,data);
			}else {
				IOS.writeMsg(SERVER_OUT,"NO_CONFIRMS");
			}
			//IOS.writeMsg(SERVER_OUT,"[" + IOS.readStr(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr") + "]");
		}
	}
	
	public void getCommands() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		System.out.println(CMD_DIR + COUNTRY + "/" + USER_NAME + ".cmd");
		if((new File(CMD_DIR + COUNTRY + "/" + USER_NAME + ".cmd").exists())) {
			IOS.writeMsg(SERVER_OUT,"[" + IOS.readStr(CMD_DIR + COUNTRY + "/" + USER_NAME + ".cmd") + "]");
		}else {
			IOS.writeMsg(SERVER_OUT,"NO_COMMANDS");
		}
	}
	
	public void postInitiate() {
		COUNTRY = DP.getValue("DEST_COUNTRY");
		USER_NAME = DP.getValue("DEST_NAME");
		new File(ACTIVE_DIR + COUNTRY).mkdir();
		if(!(new File(ACTIVE_DIR + COUNTRY + "/" + USER_NAME + ".act").exists())) {
			IOS.writeStr(ACTIVE_DIR + COUNTRY + "/" + USER_NAME + ".act",DP.getCleanJSON(),false);
			IOS.writeMsg(SERVER_OUT,"DONE");
		}else {
			String data = IOS.readStr(ACTIVE_DIR + COUNTRY + "/" + USER_NAME + ".act");
			if(data.equals("")) {
				IOS.writeStr(ACTIVE_DIR + COUNTRY + "/" + USER_NAME + ".act",DP.getCleanJSON(),false);
				IOS.writeMsg(SERVER_OUT,"DONE");
			}else {
				DP = new DataParser("[" + data + "]");
				if(DP.check4Same("NAME",DP.getValue("NAME"))) {
					System.out.println(CONN_DIR + COUNTRY + "/" + USER_NAME + ".cnt");
					
					if(new File(CONN_DIR + COUNTRY + "/" + USER_NAME + ".cnt" ).exists()) {
						if(IOS.readStr(CONN_DIR + COUNTRY + "/" + USER_NAME + ".cnt").equals("CONNECTED")) {
							IOS.writeMsg(SERVER_OUT,"CONNECTED");
						}
						if(IOS.readStr(CONN_DIR + COUNTRY + "/" + USER_NAME + ".cnt").equals("CONNECTING")) {
							System.out.println(CONN_DIR + COUNTRY + "/" + USER_NAME + ".cnt");
							IOS.writeMsg(SERVER_OUT,"CONNECTING");
						}
					}else {
						//System.out.println("kooooooooooooo");
						IOS.writeMsg(SERVER_OUT,"DONE");
					}
				}else {
					DP = new DataParser(FROM_CLIENT);
					IOS.writeStr(ACTIVE_DIR + COUNTRY + "/" + USER_NAME + ".act",("," + DP.getCleanJSON()),true);
					IOS.writeMsg(SERVER_OUT,"DONE");
				}
			}
		}
		
	}
	
	public void postRequest() {
		COUNTRY = DP.getValue("DEST_COUNTRY");
		USER_NAME = DP.getValue("DEST_NAME");
		if(!(new File(REQ_DIR + COUNTRY).exists())) {
			new File(REQ_DIR + COUNTRY).mkdir();
			IOS.writeStr(REQ_DIR + COUNTRY + "/" + USER_NAME + ".rqs", DP.getCleanJSON(),false);
		}else {
			if(!(new File(REQ_DIR + COUNTRY + "/" + USER_NAME + ".rqs").exists())) {
				IOS.writeStr(REQ_DIR + COUNTRY + "/" + USER_NAME + ".rqs", DP.getCleanJSON(),false);
			}else {
				IOS.writeStr(REQ_DIR + COUNTRY + "/" + USER_NAME + ".rqs", ("," + DP.getCleanJSON()),true);
			}
		}
		IOS.writeMsg(SERVER_OUT,"DONE");
	}
	
	public void postApproved() {
		COUNTRY = DP.getValue(1,"COUNTRY");
		USER_NAME = DP.getValue(1,"NAME");
		if(!(new File(APPR_DIR + COUNTRY).exists())) {
			new File(APPR_DIR + COUNTRY).mkdir();
			IOS.writeStr(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr", DP.getCleanJSON(),false);
		}else {
			if(!(new File(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr").exists())) {
				IOS.writeStr(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr", DP.getCleanJSON(),false);
			}else {
				IOS.writeStr(APPR_DIR + COUNTRY + "/" + USER_NAME + ".appr", ("," + DP.getCleanJSON()),true);
			}
		}
		IOS.writeMsg(SERVER_OUT,"DONE");
	}
	
	public void postConnectIn() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		new File(CONN_DIR + COUNTRY).mkdir();
		IOS.writeStr(CONN_DIR + COUNTRY + "/" + USER_NAME + ".cnt", "CONNECTING", false);
		IOS.writeMsg(SERVER_OUT,"DONE");
	}
	
	public void postFormat() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		new File(FORM_DIR + COUNTRY).mkdir();
		IOS.writeStr(FORM_DIR + COUNTRY + "/" + USER_NAME + ".fmt", DP.getValue("FORMAT"), false);
		IOS.writeMsg(SERVER_OUT,"DONE");
	}
	
	public void getFormat() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		IOS.writeMsg(SERVER_OUT,IOS.readStr(FORM_DIR + COUNTRY + "/" + USER_NAME + ".fmt"));
	}
	
	public void postConnectPass() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		new File(CONN_DIR + COUNTRY).mkdir();
		IOS.writeStr(CONN_DIR + COUNTRY + "/" + USER_NAME + ".cnt", "CONNECTED", false);
		IOS.writeMsg(SERVER_OUT,"CONNECTED");
	}
	
	public void postCmd() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		new File(CMD_DIR + COUNTRY).mkdir();
		IOS.writeStr(CMD_DIR + COUNTRY + "/" + USER_NAME + ".cmd", DP.getCleanJSON(), false);
		IOS.writeMsg(SERVER_OUT,"DONE");
	}
	
	public void getCmd() {
		COUNTRY = DP.getValue("COUNTRY");
		USER_NAME = DP.getValue("NAME");
		IOS.writeMsg(SERVER_OUT,IOS.readStr(CMD_DIR + COUNTRY + "/" + USER_NAME + ".cnt"));
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
