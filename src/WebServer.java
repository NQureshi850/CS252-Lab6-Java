import java.util.*;

//import org.apache.http.*;
//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.deser.ValueInstantiators.Base;

import com.sun.net.httpserver.*;

//import net.thegreshams.firebase4j.error.*;
//import net.thegreshams.firebase4j.model.*;

import java.io.*;
import java.net.*;

public class WebServer {

	static int port = 5001;
	private static Firebase base = null;
	private static FirebaseResponse response = null;

	static class getUserHandler implements HttpHandler 
	{
		public void handle(HttpExchange he) throws IOException 
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			int code = parseBody(query, parameters);

			/*
				String httpResponse = "You suck";
				he.sendResponseHeaders(400, httpResponse.length());
				OutputStream os = he.getResponseBody();
				os.write(httpResponse.toString().getBytes());
				os.close();
			 */


			String httpResponse = "";
			for (String key : parameters.keySet())
			{
				httpResponse += key /*+ " = " + parameters.get(key)*/ + "\n";
				System.out.println("getUser" + key);
				key = key.substring(1,  key.length()-1);

				String parts[] = key.split(",");
				String username = parts[0];
				String gameName = parts[1];

				System.out.println(username);
				username = username.substring(username.indexOf(":") + 2, username.length() -1);
				gameName = gameName.substring(gameName.indexOf(":") + 2, gameName.length() -1);
				System.out.println("game: " + gameName);

				/*if(data[1].charAt(0) == '\"' && data[1].charAt(data[1].length()) == '\"')
						{
							data[1] = data[1].substring(1, data[1].length()-1);
						}
				 */

				System.out.println(username);
				Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
				dataMap.put(username, "100");

				try {
					response = base.get(gameName);
				} catch (FirebaseException e) {
					//e.printStackTrace();
				}

				String responseString = response.toString();

				if(responseString.indexOf(username) == -1)
				{
					try {
						response = base.patch(gameName, dataMap);
					} catch (JacksonUtilityException e) {
						e.printStackTrace();
					} catch (FirebaseException e) {
						e.printStackTrace();
					}

					System.out.println("reached");
					responseString = response.toString();

					responseString = responseString.substring(responseString.indexOf(username));

					responseString = responseString.substring(0, responseString.indexOf("Raw-body")-1);
					System.out.println("Pre-responseString: " + responseString);

					if(responseString.indexOf(",") == -1)
						responseString = responseString.substring(responseString.indexOf("=") + 1, responseString.indexOf("})"));

					else
						responseString = responseString.substring(responseString.indexOf("=") + 1, responseString.indexOf(","));
				}

				else
				{
					responseString = responseString.substring(responseString.indexOf(username));

					responseString = responseString.substring(0, responseString.indexOf("Raw-body")-1);
					System.out.println("Pre-responseString: " + responseString);


					if(responseString.indexOf(",") == -1)
						responseString = responseString.substring(responseString.indexOf("=") + 1, responseString.indexOf("})"));

					else
						responseString = responseString.substring(responseString.indexOf("=") + 1, responseString.indexOf(","));

					System.out.println("ResponseString: " + responseString);

				}



				httpResponse = "{\"score\": " + responseString + "}";
				System.out.println(httpResponse);

			}

			//FirebaseResponse[ (Success:true) (Code:200) (Body:{Person1=100, Person2=100}) (Raw-body:{"Person1":100,"Person2":100}) ]

			he.sendResponseHeaders(200, httpResponse.length());
			OutputStream os = he.getResponseBody();
			os.write(httpResponse.toString().getBytes());
			os.close();


		}

	}

	static class getScoresForGameHandler implements HttpHandler 
	{
		public void handle(HttpExchange he) throws IOException 
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			int code = parseBody(query, parameters);

			/*
				String httpResponse = "You suck";
				he.sendResponseHeaders(400, httpResponse.length());
				OutputStream os = he.getResponseBody();
				os.write(httpResponse.toString().getBytes());
				os.close();
			 */


			String httpResponse = "";
			for (String key : parameters.keySet())
			{
				httpResponse += key /*+ " = " + parameters.get(key)*/ + "\n";
				System.out.println("getUser" + key);
				key = key.substring(1,  key.length()-1);

				String gameName = key;

				gameName = gameName.substring(gameName.indexOf(":") + 2, gameName.length() -1);
				System.out.println("game: " + gameName);

				/*if(data[1].charAt(0) == '\"' && data[1].charAt(data[1].length()) == '\"')
						{
							data[1] = data[1].substring(1, data[1].length()-1);
						}
				 */

				try {
					response = base.get(gameName);
				} catch (FirebaseException e) {
					//e.printStackTrace();
				}

				String responseString = response.toString();

				responseString = responseString.substring(0, responseString.indexOf("Raw-body")-1);
				System.out.println("Pre-responseString: " + responseString);


				responseString = responseString.substring(responseString.indexOf("{") + 1, responseString.indexOf("})"));

				System.out.println("ResponseString: " + responseString);


				String temp = "";
				String individualScores[] = responseString.split(",");
				
				for(String score : individualScores)
				{
					String pairs[] = score.split("=");
					temp += "\"" + pairs[0] + "\": " + pairs[1] + ",";
				}
				
				temp = temp.substring(0, temp.length() - 1);
				//FirebaseResponse[ (Success:true) (Code:200) (Body:{Person1=100, Person2=100}) (Raw-body:{"Person1":100,"Person2":100}) ]


				responseString = temp;
				httpResponse = "{\"scores\": [" + responseString + "]}";
				System.out.println(httpResponse);

			}


			he.sendResponseHeaders(200, httpResponse.length());
			OutputStream os = he.getResponseBody();
			os.write(httpResponse.toString().getBytes());
			os.close();


		}

	}

	static class updateScoreHandler implements HttpHandler
	{
		public void handle(HttpExchange he) throws IOException 
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			int code = parseBody(query, parameters);

			/*
			if(code == 0)
			{
				String httpResponse = "You suck";
				he.sendResponseHeaders(400, httpResponse.length());
				OutputStream os = he.getResponseBody();
				os.write(httpResponse.toString().getBytes());
				os.close();
			}
			 */
			String httpResponse = "";
			for (String key : parameters.keySet())
			{
				httpResponse += key + /*" = " + parameters.get(key) + */"\n";
				System.out.println("update" + key);
				key = key.substring(1,  key.length()-1);

				String parts[] = key.split(",");
				String username = parts[0];
				String gameName = parts[1];
				String toUpdate = parts[2];

				System.out.println(username);
				username = username.substring(username.indexOf(":") + 2, username.length() -1);
				gameName = gameName.substring(gameName.indexOf(":") + 2, gameName.length() -1);
				toUpdate = toUpdate.substring(toUpdate.indexOf(":") + 1, toUpdate.length());
				System.out.println("toUpdate: " + toUpdate);

				/*if(data[1].charAt(0) == '\"' && data[1].charAt(data[1].length()) == '\"')
						{
							data[1] = data[1].substring(1, data[1].length()-1);
						}
				 */


				System.out.println(username);
				Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
				dataMap.put(username, toUpdate);


				try {
					response = base.patch(gameName, dataMap);
				} catch (FirebaseException e) {
					//e.printStackTrace();
				} catch (JacksonUtilityException e) {
					//e.printStackTrace();
				}

				String responseString = response.toString();
				System.out.println("Response " + responseString);

				httpResponse = "";
				System.out.println(httpResponse);


			}
			he.sendResponseHeaders(200, httpResponse.length());
			OutputStream os = he.getResponseBody();
			os.write(httpResponse.toString().getBytes());
			os.close();


		} 
	}

	static class HomePageHandler implements HttpHandler
	{
		public void handle(HttpExchange he) throws IOException 
		{
//			File index = new File("../CS252-Lab6/index.html");
//			FileReader fr = new FileReader(index);
//			char[] buf = new char[8092];
//			fr.read(buf);
			
			String rootPath = "../../CS252-Lab6";
			URI url = he.getRequestURI();
			System.out.println(url);
			String path = url.getPath();
			if (path.equals("/")) {
				path = "/index.html";
			}
			File file = new File(rootPath + path).getCanonicalFile();
			
			System.out.println(rootPath + path);
			
			if (!file.isFile()) {
				System.out.println("Fail");
			}
			else {
				// Object exists and is a file: accept with response code 200.
	              String mime = "text/html";
	              if(path.substring(path.length()-3).equals(".js")) mime = "application/javascript";
	              if(path.substring(path.length()-3).equals("css")) mime = "text/css";            

	              Headers h = he.getResponseHeaders();
	              h.set("Content-Type", mime);
	              he.sendResponseHeaders(200, 0);              

	              OutputStream os = he.getResponseBody();
	              FileInputStream fs = new FileInputStream(file);
	              final byte[] buffer = new byte[0x10000];
	              int count = 0;
	              while ((count = fs.read(buffer)) >= 0) {
	                os.write(buffer,0,count);
	              }
	              fs.close();
	              os.close();
			}

		} 
	}

	public static int parseBody(String query, Map<String, Object> parameters) throws UnsupportedEncodingException
	{
		if(query == null)
		{
			return 0;
		}

		else
		{
			String pairs[] = query.split("&");
			for(String pair: pairs)
			{
				String param[] = pair.split("=");
				String key = null;
				String value = null;

				if(param.length > 0)
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));

				if(param.length > 1)
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));

				if(parameters.containsKey(key))
				{
					Object object = parameters.get(key);

					if(object instanceof List<?>)
					{
						List<String> values = (List<String>) object;

						values.add(value);
					}

					else if(object instanceof String)
					{
						List<String> values = new ArrayList<String>();
						values.add((String) object);
						values.add(value);
						parameters.put(key,  values);
					}
				}

				else
				{
					parameters.put(key, value);
				}
			}

			return 1;
		}
	}

	public static void main(String[] args) throws FirebaseException, IOException
	{
		base = new Firebase("https://cs252-fc67c.firebaseio.com/");

		//Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
		//dataMap.put( "test1", "10" );
		//base.put("test2/test3", dataMap);

		/*DefaultBHttpServerConnection connection = new DefaultBHttpServerConnection(32406);
		connection.bind(server.accept());
		HttpRequest request = connection.receiveRequestHeader();
		connection.receiveRequestEntity((HttpEntityEnclosingRequest)request);
		HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
		System.out.println(EntityUtils.toString(entity));
		 */

		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new HomePageHandler());
		server.createContext("/getUser", new getUserHandler());
		server.createContext("/updateScore", new updateScoreHandler());
		server.createContext("/getScoresForGame", new getScoresForGameHandler());
		server.setExecutor(null);
		server.start();

		/*ServerSocket server = new ServerSocket(port);

		while(true)
		{
			Socket clientRequest = server.accept();
			System.out.println("got it");
			PrintWriter writer = new PrintWriter(clientRequest.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientRequest.getInputStream()));
			String line = null;

			writer.println("hello");

			while((line = reader.readLine()) != null)
			{
				System.out.println(line);

			}
		}
		 */

	}
}
