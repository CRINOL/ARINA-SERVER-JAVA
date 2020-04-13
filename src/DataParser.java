
import java.io.*; 
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.util.*;
import java.text.*;

public class DataParser {

    private JSONArray DATA_ARRAY = null;
    private JSONObject DATA_FIELD = null;
    private JSONParser DATA_PARSER = null;
    private Object JSON_OBJECT = null;
    private String DATA_STRING = "";
    private String JSON_STRING = "";
    ;
    private int DEL_POSITION = 0;

    public DataParser(String JSON_STRING) {
        try {
            this.JSON_STRING = JSON_STRING;
            DATA_FIELD = new JSONObject();
            DATA_PARSER = new JSONParser();
            JSON_OBJECT = DATA_PARSER.parse(this.JSON_STRING);
            DATA_ARRAY = (JSONArray) JSON_OBJECT;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataParser() {
        DATA_FIELD = new JSONObject();
    }


    /*public String addBond(String DATA,String NAME,String COUNTRY,String STATE){
        this.DATA_STRING = DATA;
        try{
            DATA_FIELD.put("NAME",NAME);
            DATA_FIELD.put("COUNTRY",COUNTRY);
            DATA_FIELD.put("STATE",STATE);
            if(DATA.equals("") || DATA.length() < 5){
                DATA_STRING = "[" + DATA_FIELD + "]";
            }else{
                StringBuffer data_buffer = new StringBuffer(this.DATA_STRING);
                this.DATA_STRING = String.valueOf(data_buffer.insert((this.DATA_STRING.length() - 1),("," +DATA_FIELD)));
            }
            return DATA_STRING;
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }*/

    public JSONObject getObj(int index){
        Object data_field = (JSONObject)DATA_ARRAY.get(index);
        return (JSONObject)data_field;
    }

    public JSONArray getArray(){
        return this.DATA_ARRAY;
    }

    public String getValue(int pos,String key){
        return String.valueOf(getObj(pos).get(key));
    }
    
    public String getValue(String key) {
    	return String.valueOf(getObj(0).get(key));
    }
    
    public String getCleanJSON() {
    	StringBuffer sb = new StringBuffer(this.JSON_STRING);
    	sb.delete(0, 1);
    	sb.delete(JSON_STRING.length() - 2, JSON_STRING.length());
    	return sb.toString();
    }

    public String[] getValues(String criteria){
        String[] values = new String[DATA_ARRAY.size()];
        for(int x = 0;x < values.length;x++){
            values[x] = getValue(x,criteria);
        }
        return values;
    }
    
    public String deleteObj(){
        DATA_ARRAY.remove(DEL_POSITION);
        return String.valueOf(DATA_ARRAY);
    }

    public String deleteObj(String CRITERIA,String SCAN_TARGET){
        int pos = getIndex(CRITERIA,SCAN_TARGET);
        DATA_ARRAY.remove(pos);
        return String.valueOf(DATA_ARRAY);
    }
    
    public String removeObjs(String CRITERIA,String CHECKER) {
    	int k = 0;
    	for(Object json_obj:DATA_ARRAY) {
    		JSONObject json_objx = (JSONObject)json_obj;
    	    if(json_objx.get(CRITERIA).equals(CHECKER)) {
    	    	DATA_ARRAY.remove(k);
    	    }
    	    k++;
    	}
    	return DATA_ARRAY.toString();
    }

    public int getIndex(String ctr,String tgt){
        int i = 0;
        for(int x = 0;x < getTotalItems();x++){
            JSONObject data_obj = (JSONObject)DATA_ARRAY.get(x);
            if(String.valueOf(data_obj.get(ctr)).equals(tgt)){
                i = x;
                break;
            }
        }
        return i;
    }
    
    public int getTotalItems(){
        return DATA_ARRAY.size();
    }

    public int getTotalItems(JSONObject J_OBJ){
        return J_OBJ.size();
    }
    
    public boolean check4Same(String CRITERIA,String CHECKER){
        boolean same = false;
        try {
            for(int i = 0;i < DATA_ARRAY.size();i++) {
                JSONObject JSON_OBJ = (JSONObject)DATA_ARRAY.get(i);
                System.out.println(JSON_OBJ.get(CRITERIA));
                if (String.valueOf(JSON_OBJ.get(CRITERIA)).equals(CHECKER)) {
                    //Log.i("checker","true" + String.valueOf(i));
                    same = true;
                    DEL_POSITION = i;
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return same;
    }
    
    public String getIndexedPair(String KEY,int INDEX,String VALUE) {
    	JSONObject JSON_OBJ = new JSONObject();
    	JSON_OBJ.put(KEY + String.valueOf(INDEX), VALUE);
    	return String.valueOf(JSON_OBJ);
    }

}