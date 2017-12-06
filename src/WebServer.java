import java.util.*;

import org.apache.http.*;
import org.apache.http.impl.*;
import org.apache.http.util.*;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.deser.ValueInstantiators.Base;

import com.sun.net.httpserver.*;

import net.thegreshams.firebase4j.error.*;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.service.Firebase;
import net.thegreshams.firebase4j.model.*;

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

			if(code == 0)
			{
				String httpResponse = "You suck";
				he.sendResponseHeaders(400, httpResponse.length());
				OutputStream os = he.getResponseBody();
				os.write(httpResponse.toString().getBytes());
				os.close();
			}

			else
			{
				String httpResponse = "";
				for (String key : parameters.keySet())
				{
					httpResponse += key /*+ " = " + parameters.get(key)*/ + "\n";
					System.out.println(key);
				}
				he.sendResponseHeaders(200, httpResponse.length());
				OutputStream os = he.getResponseBody();
				os.write(httpResponse.toString().getBytes());
				os.close();
			}

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

			if(code == 0)
			{
				String httpResponse = "You suck";
				he.sendResponseHeaders(400, httpResponse.length());
				OutputStream os = he.getResponseBody();
				os.write(httpResponse.toString().getBytes());
				os.close();
			}

			else
			{
				String httpResponse = "";
				for (String key : parameters.keySet())
					httpResponse += key + /*" = " + parameters.get(key) + */"\n";
				he.sendResponseHeaders(200, httpResponse.length());
				OutputStream os = he.getResponseBody();
				os.write(httpResponse.toString().getBytes());
				os.close();
			}

		} 
	}

	static class HomePageHandler implements HttpHandler
	{
		public void handle(HttpExchange he) throws IOException 
		{
			String homepage = "<h1> Test Page </h1>";
			he.sendResponseHeaders(200, homepage.length());
			OutputStream os = he.getResponseBody();
			os.write(homepage.getBytes());
			os.close();

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

	public static void main(String[] args) throws FirebaseException, JsonParseException, JsonMappingException, IOException, JacksonUtilityException, HttpException
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
