import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;



/**
 * @author Kumudini
 *
 */

public class server implements Runnable{

	private static final int PORT = 8080;
	private static ServerSocket serverSocket;
	protected static HashMap<String,Integer> fileRequested = new HashMap<String,Integer>();
	
	private Socket socket;
	private String request;
	
	public server(Socket socket) {
		this.request = null;
		this.socket = socket;
	}

	
	public static void main(String[] args) throws IOException {
	
			System.out.println("HTTPSERVER Details:");
			System.out.println("Server Machine IP : "+ InetAddress.getLocalHost().getCanonicalHostName());
			System.out.println("Server connection Port number: " + PORT);
			System.out.println("==========================================================");

			System.out.println("Starting HTTP server..");
			serverSocket = new ServerSocket(PORT);
			start();
			
	}
	
	private static void start() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			server serverConnection = new server(socket);

			Thread request = new Thread(serverConnection);
			request.start();
		}
	}
	
	
	public void run() {
		try {
			RequestHandler();
		} catch (Exception e) {
			System.err.println("Error Occured: " + e.getMessage());
			try {
				socket.close();
				System.exit(0);
			} catch (IOException e1) {
				System.err.println("Error Closing socket Connection.");
				System.exit(0);
			}
			System.err.println("Server is Terminating!");
		}
	}

	/**
	 * @throws Exception
	 */
	private void RequestHandler() throws Exception {
		InputStream inputStream;
		OutputStream outputStream;
		//
		File current = new File(System.getProperty("user.dir")); 
		File www = new File(current.getParent() + File.separator  +"www");
		//System.out.println(root.getName());
		
		if (www.isDirectory()) {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			serverRequestHandler(inputStream, outputStream, www.toString());			
			outputStream.close();
			inputStream.close();
		} else {
			throw new Exception("www directory not present!");
		}
		socket.close();
	}

	/**
	 * @param input
	 * @param output
	 * @param root 
	 * @throws Exception
	 */
	private void serverRequestHandler(InputStream input, OutputStream output, String root) throws Exception {
		String line;
		BufferedReader bufferREader = new BufferedReader(new InputStreamReader(input));
		while ((line = bufferREader.readLine()) != null) {
			if (line.length() <= 0) {
				break;
			}
			if (line.startsWith("GET")) {
				String filename = line.split(" ")[1].substring(1);
				File resource = new File(root + File.separator + filename);
				if (resource.exists()) {
					request = filename;
					populateRequestResponse(resource, output);
					this.printResult(request, socket.getPort(), socket.getRemoteSocketAddress().toString().split(":")[0].replace("/", ""));
				} else {
					String Content_NOT_FOUND = "<html><head></head><body><h1>Requested File Not Found</h1></body></html>";
					
					String REQ_NOT_FOUND = "HTTP/1.0 404 Not Found\n\n";
					String header = REQ_NOT_FOUND + Content_NOT_FOUND;
					output.write(header.getBytes());
					this.printResult(filename);
				}
				break;
			}
		}
	}
	


	/**
	 * 
	 * @param request
	 * @param port
	 * @param ipAddress
	 */
	public void printResult(String request, int port, String ipAddress) {
		if(server.fileRequested.get(request) == null) {
			server.fileRequested.put(request, 1);
		} else {
			server.fileRequested.put(request, server.fileRequested.get(request) + 1);
		}
		System.out.println(request + " | "+ ipAddress + " | " + port +" | " + server.fileRequested.get(request));
		System.out.println();
	}
	
	/** 
	 * @param filename
	 */
	private void printResult(String filename) {
		System.out.println(filename + " not found, this throws error 404..");
		System.out.println();
	}
	
	/**
	 * @param resource
	 * @param output
	 * @throws IOException
	 */
	private void populateRequestResponse(File resource, OutputStream output) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		
		String Request_Found = "HTTP/1.0 200 OK\n";
		String SERVER = "Server: HTTP server/0.1\n";
		String DATE = "Date: " + dateFormat.format(new java.util.Date()) + "\n";
		
		
		File file = new File(resource.getAbsolutePath());
	
	    //MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
	    //String mimeType = fileTypeMap.getContentType(file.getName());
	    /*if(mimeType == null)
	    {
		    mimeType = "application/octet-stream";

	    }
	    */
		
		String CONTENT_TYPE;
		String type = Files.probeContentType(file.toPath());
		if(type == null) type = "application/octet-stream";
		CONTENT_TYPE = "Content-type: " + type +"\n";
		
		
		//String CONTENT_TYPE = "Content-type: " + URLConnection.guessContentTypeFromName(resource.getName()) + "\n";
	
		
	    Long lastModified = file.lastModified();
	    Date date = new Date(lastModified);
	    
	    String LastModifiedDate = "Last-Modified: " + dateFormat.format(date) + "\n";	   

		String LENGTH = "Content-Length: " + (resource.length()) + "\n\n";
		String header = Request_Found + DATE + SERVER + LastModifiedDate + CONTENT_TYPE  + LENGTH;
		System.out.println(header);
		output.write(header.getBytes());
		
		Files.copy(Paths.get(resource.toString()), output);
		output.flush();
		
		
	}
}