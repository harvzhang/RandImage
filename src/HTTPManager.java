import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Class that manages the HTTP Request Sending and Response Parsing
 */

public class HTTPManager {
	
	/*
	 * Returns an ArrayList of integers received from sending HTTP request to
	 * https://www.random.org/integers/?
	 * This method blocks until the HTTP request is completed
	 * 
	 * @param  min  The smallest value allowed for each integer
	 * @param  max  The largest value allowed for each integer
	 * @param  n  The number of integers requested
	 * @param  col  The number of columns in which the integers will be arranged
	 * @return      The list of integer returned by the HTTP request
	 */
	public static ArrayList<Integer> getRandInts(int min, int max, int n, int col) 
			throws MalformedURLException, IOException {
		
		ArrayList<Integer> output = new ArrayList<Integer> ();
		
		try {
			final int LIMIT = 10000;
			final int TIMEOUT = 120000;
			
			//Break requests that exceed request limit into several 
			//chunks and send each chunk separately
			do {
				//send the request to specified URL
				int num = (n > LIMIT) ? LIMIT : n;
				String param = getRandParamString(min, max, num, col); 
				URL url = new URL("https://www.random.org/integers/?"+param);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				
				sendHTTPRequest(con, TIMEOUT);
				
				//Parse the response status
				int status = con.getResponseCode();
				System.out.println("DEBUG: the response status code is " + status);
				
				List<String> rsp = getHTTPRspMsg(con);
				ArrayList<Integer> out = parseStringToInts(rsp);
				
				//Concatenate the request response to output
				output.addAll(out);
				
				con.disconnect();
				n -= LIMIT;
			} while (n > 0);
			
		} catch (ProtocolException e) {
			e.printStackTrace();
			System.out.println("Error in underlying protocol");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("Encoding Unsupported");
		}
		
		return output;
	}
	
	/*
	 * Configures an existing connection for HTTP request sending
	 * Returns the connection that is opened
	 * 
	 * @param  conURL The URL to connect to
	 * @param  timeout  The connection timeout time in milliseconds
	 */
	public static HttpURLConnection sendHTTPRequest (HttpURLConnection con, int timeout) 
			throws IOException {
		con.setRequestMethod("GET");
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		con.setDoOutput(true);
		
		return con;
	}
	
	/*
	 * Collects the response messages into a List and returns a list of 
	 * HTTP response messages
	 * 
	 * @param con HttpURLConnection object for the current connection
	 * @return List of String response messages
	 */
	private static List<String> getHTTPRspMsg (HttpURLConnection con) throws IOException {
		BufferedReader inStream = new BufferedReader(
				  new InputStreamReader(con.getInputStream()));
		String inputLine;
		ArrayList<String> output = new ArrayList<String> ();
		while ((inputLine = inStream.readLine()) != null) {
			output.add(inputLine);
		}
		inStream.close();
		return output;
	}
	
	/*
	 * Parses the HTTP request parameters to a single string
	 * 
	 * @param  min  The smallest value allowed for each integer
	 * @param  max  The largest value allowed for each integer
	 * @param  n  The number of integers requested
	 * @param  col  The number of columns in which the integers will be arranged
	 * @return      A single string that is the parameters of the request
	 */
	private static String getRandParamString(int min, int max, int num, int col) 
			throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<>();
		params.put("num", Integer.toString(num));
		params.put("min", Integer.toString(min));
		params.put("max", Integer.toString(max));
		params.put("col", Integer.toString(col));
		params.put("base", "10");
		params.put("format", "plain");
		params.put("rnd", "new");
		
		return getParamString(params);
	}
	
	/*
	 * Parses the response strings to a list of integers
	 * 
	 * @param input List of Strings
	 * @return ArrayList of Integers parsed from HTTP response messages
	 */
	private static ArrayList<Integer> parseStringToInts(List<String> input) {
		ArrayList<Integer> output = new ArrayList<Integer> ();
		
		for (String entry : input) {
			entry.replaceAll("\\s+"," ");
			//split the strings by tab character
			String[] tokens = entry.split("\\t");
			for (int i = 0; i < tokens.length; i++) {
				output.add(Integer.parseInt(tokens[i]));
			}
		}
		
		return output;
	}
	
	/*
	 * Parses the HTTP request parameters to a single string
	 * 
	 * @param params Map of parameters with Key = parameter name & Value = parameter value
	 * @returns A single string that is the parameters of the HTTP request
	 */
	public static String getParamString(Map<String, String> params) 
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		
		for (Map.Entry<String, String> entry : params.entrySet()) {
			builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			builder.append("&");
		}
		
		String output = builder.toString();
		if (output.length() != 0)
			output = output.substring(0, output.length() - 1);
		return output;
	}
}
