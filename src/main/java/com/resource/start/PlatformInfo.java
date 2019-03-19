package com.resource.start;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PlatformInfo {
	protected CloseableHttpClient httpClient;
	protected HttpContext httpContext;
	protected String bPMSURI;

	public PlatformInfo() {

	}

	public PlatformInfo(CloseableHttpClient client, String bPMSURI) {
		this.httpClient = client;
		this.bPMSURI = bPMSURI;
	}

	public String dologin(String username, String password, String loginUrl) {
		try {
			CookieStore cookieStore = new BasicCookieStore();
			httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", password));
			urlParameters.add(new BasicNameValuePair("redirect", "false"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(urlParameters, "utf-8");
			executePostRequest(bPMSURI + loginUrl, entity);
			return getCookieValue(cookieStore, "X-Bonita-API-Token");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

	}

	public int executePostRequest(String uri, UrlEncodedFormEntity entity) {
		// TODO Auto-generated method stub
		HttpPost postRequest = new HttpPost(uri);
		postRequest.setEntity(entity);
		HttpResponse response = null;
		try {

			response = httpClient.execute(postRequest, httpContext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ensureStatusOk(response);
	}

	public String getCookieValue(CookieStore cookieStore, String cookieName) {
		// TODO Auto-generated method stub
		String value = null;
		for (Cookie cookie : cookieStore.getCookies()) {
			if (cookie.getName().equals(cookieName)) {
				value = cookie.getValue();
				break;
			}
		}
		return value;
	}

	public PoolingHttpClientConnectionManager getConnectionManager() {
		{
			// TODO Auto-generated method stub
			PoolingHttpClientConnectionManager conMan = new PoolingHttpClientConnectionManager();
			conMan.setMaxTotal(10000);
			conMan.setDefaultMaxPerRoute(10000);
			return conMan;
		}
	}

	// The user ID from the platform
	public long getactorID(String userName, String tokencsrf, String url) {
		// TODO Auto-generated method stub
		long id1 = 0;
		HttpResponse response = executeGetRequest(url + userName, tokencsrf);
		String actorJson;
		JSONArray array = null;
		try {
			actorJson = EntityUtils.toString(response.getEntity());
			array = (JSONArray) new JSONParser().parse(actorJson);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("The size of the array !!"+array.size() );
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = null;
			json = (JSONObject) array.get(i);
			String id = (String) json.get("id");
			id1 = Long.valueOf(Long.parseLong(id, 10));
		}
		return id1;
	}

	public HttpResponse executeGetRequest(String apiURI, String tokencsrf) {
		// TODO Auto-generated method stub
		try {

			HttpGet getrequest = new HttpGet(bPMSURI + apiURI);
			getrequest.setHeader("X-Bonita-API-Token", tokencsrf);
			HttpResponse response = httpClient.execute(getrequest, httpContext);
			ensureStatusOk(response);
			return response;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int ensureStatusOk(HttpResponse response) {
		// TODO Auto-generated method stub
		if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
			System.out.println("Failed: Http error code : " + response.getStatusLine().getStatusCode() + " : "
					+ response.getStatusLine().getReasonPhrase() + " in the method ");
		}
		return response.getStatusLine().getStatusCode();
	}
	

	public ArrayList<TaskInfo> retrieveTask(long userId, String token, String url) {
		//System.out.println(url);
		ArrayList<TaskInfo> listofTasks = new ArrayList<TaskInfo>();
		HttpResponse response = executeGetRequest(url + userId, token);
		String Data;
		try {
			Data = EntityUtils.toString(response.getEntity());
			// System.out.println("The value of the data!!"+Data);
			JSONArray array = (JSONArray) new JSONParser().parse(Data);
			// System.out.println("The size of the array !!"+array.size() );
			for (int i = 0; i < array.size(); i++) {
				JSONObject json = null;
				json = (JSONObject) array.get(i);
				String id = (String) json.get("id");
				long id1 = Long.valueOf(Long.parseLong(id, 10));
				String name = (String) json.get("name");
				String state = (String) json.get("state");
				String timestamp = (String) json.get("reached_state_date");
				String rootcaseId = (String) json.get("rootCaseId");
				String actor = (String) json.get("assigned_id");
				String assigneddate = (String) json.get("assigned_date");

				// long actorid = Long.valueOf(Long.parseLong(actor, 10));
				TaskInfo task = new TaskInfo(id1, name, state, timestamp, rootcaseId, actor, assigneddate);
				listofTasks.add(task);

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listofTasks;
	}

	public ArrayList<CaseInfo> archivedcases(String token, String url)
			throws ParseException, IOException, org.json.simple.parser.ParseException {
		ArrayList<CaseInfo> cases = new ArrayList<CaseInfo>();
		//System.out.println(url);
		HttpResponse response = executeGetRequest(url, token);
		String Data;
		Data = EntityUtils.toString(response.getEntity());
		//System.out.println("The value of the data!!" + Data);
		JSONArray array = (JSONArray) new JSONParser().parse(Data);
		//System.out.println("The size of the array !!" + array.size());
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = null;
			json = (JSONObject) array.get(i);
			String caseid = (String) json.get("sourceObjectId");
			String processname = (String) json.get("processDefinitionId");
			String startDate = (String) json.get("start");
			String end_date = (String) json.get("end_date");
			String archivedate = (String) json.get("archivedDate");
			String startby = (String) json.get("started_by");
			CaseInfo thecase = new CaseInfo(caseid, processname, startDate, end_date, archivedate, startby);
			cases.add(thecase);
		}

		return cases;

	}

	public ArrayList<TaskInfo> archivedTask(String token,long idg, String url)
			throws ParseException, IOException, org.json.simple.parser.ParseException {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		//System.out.println(url);
		HttpResponse response = executeGetRequest(url+idg, token);
		String Data;
		Data = EntityUtils.toString(response.getEntity());
		//System.out.println("The value of the data!!" + Data);
		JSONArray array = (JSONArray) new JSONParser().parse(Data);
		//System.out.println("The size of the array !!" + array.size());
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = null;
			json = (JSONObject) array.get(i);
			String id = (String) json.get("sourceObjectId");
			long id1 = Long.valueOf(Long.parseLong(id, 10));
			String name = (String) json.get("name");
			String state = (String) json.get("state");
			String timestamp = (String) json.get("reached_state_date");
			String rootcaseId = (String) json.get("rootCaseId");
			String actor = (String) json.get("assigned_id");
			String assigneddate = (String) json.get("assigned_date");
			// long actorid = Long.valueOf(Long.parseLong(actor, 10));
			TaskInfo task = new TaskInfo(id1, name, state, timestamp, rootcaseId, actor, assigneddate);
			// long actorid = Long.valueOf(Long.parseLong(actor, 10));
			tasks.add(task);
		}

		return tasks;
	}

	public void autoAssign(long taskId, long userId, String token, String urlTask) {
		// TODO Auto-generated method stub
		String payloadAsString = "{\"assigned_id\":\"" + userId + "\"}";
		//System.out.println("------" + urlTask + taskId + payloadAsString);
		HttpResponse response = executePutRequest(urlTask + taskId, payloadAsString, token);
		ensureStatusOk(response);
	}

	public void schedule(long taskId, long userId, String token, String urlTask) {
		// TODO Auto-generated method stub
		String payloadAsString = "{\"actorId\":\"" + userId + "\"}";
		HttpResponse response = executePutRequest(urlTask + taskId, payloadAsString, token);
		ensureStatusOk(response);
	}

	public HttpResponse executePutRequest(String apiURI, String payloadString, String tokenCSRF) {
		// TODO Auto-generated method stub
		HttpPut putRequest = new HttpPut(bPMSURI + apiURI);
		putRequest.addHeader("Accept", "application/json");
		putRequest.setHeader("Content-Type", "application/json");
		putRequest.addHeader("X-Bonita-API-Token", tokenCSRF);
		StringEntity input;
		HttpResponse response = null;
		try {
			input = new StringEntity(payloadString);
			putRequest.setEntity(input);
			response = httpClient.execute(putRequest, httpContext);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			//System.out.println("error");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ensureStatusOk(response);
		return response;

	}

	public void executeTask(long activityId, long userId, String token, String urlstateTask) {
		String payloadAsString = "{\"state\":\"completed\"}";
		HttpResponse response = executePutRequest(urlstateTask + activityId, payloadAsString, token);
		ensureStatusOk(response);

	}

	// Ordonner les listes d'une maniere ascendante selon ready date
	public Comparator<TaskInfo> readydateComparatorFIFO = new Comparator<TaskInfo>() {
		public int compare(TaskInfo task1, TaskInfo task2) {
			// SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			int compare = 0;
			try {
				Date date1 = sdf.parse(task1.getTimestamp());
				Date date2 = sdf.parse(task2.getTimestamp()); //
				Long taskdate1 = date1.getTime();
				Long taskdate2 = date2.getTime();
				compare = taskdate1.compareTo(taskdate2);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return compare;
		}

	};

	// Ordonner les listes d'une maniere descendante selon ready date

	public Comparator<TaskInfo> readydateComparatorLIFO = new Comparator<TaskInfo>() {
		public int compare(TaskInfo task1, TaskInfo task2) {
			// SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			int compare = 0;
			try {
				Date date1 = sdf.parse(task1.getTimestamp());
				Date date2 = sdf.parse(task2.getTimestamp()); //
				Long taskdate1 = date1.getTime();
				Long taskdate2 = date2.getTime();
				compare = taskdate2.compareTo(taskdate1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return compare;
		}

	};

	// Get all the time intervals : Segmentation of the periods

	public Vector<Vector<String>> timeIntervals(Date startDate, Date endDate)
			throws ParseException, java.text.ParseException {
		Vector<Vector<String>> tvector = new Vector<Vector<String>>();
		Vector<String> t = new Vector<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		// System.out.println(" $$$$$$ "+cal.getTime().toString());
		while (cal.getTime().compareTo(endDate) < 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date1 = sdf.parse(cal.getTime().toString());
			SimpleDateFormat sdf2 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
			t.add(sdf2.format(date1));
			cal.add(Calendar.MINUTE, 1);
			Date date2 = sdf.parse(cal.getTime().toString());
			t.add(sdf2.format(date2));
			tvector.add(t);
			t = new Vector<String>();
		}
		// System.out.println("The number of working days is " + tvector.size());
		// System.out.println(tvector);
		return tvector;
	}

	public Map<Integer, Vector<TaskInfo>> listperSlot(Vector<Vector<String>> vect, ArrayList<TaskInfo> tasks) {

		Map<Integer, Vector<TaskInfo>> maplist = new HashMap<Integer, Vector<TaskInfo>>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy");
		// System.out.println(vect.size()+" ---------- "+tasks.size());
		for (int i = 0; i < vect.size(); i++) {
			LocalDateTime date1 = null;
			LocalDateTime date2 = null;
			date1 = LocalDateTime.parse(vect.get(i).elementAt(0), formatter1);
			date2 = LocalDateTime.parse(vect.get(i).elementAt(1), formatter1);
			Vector<TaskInfo> e = new Vector<TaskInfo>();
			// System.out.println(date1.toString()+ " "+date2.toString());
			for (int j = 0; j < tasks.size(); j++) {
				LocalDateTime date11 = null;
				LocalDateTime date12 = null;
				date11 = LocalDateTime.parse(tasks.get(j).timestamp, formatter);
				date12 = LocalDateTime.parse(tasks.get(j).timestamp, formatter);
				if (date11.isAfter(date1) && date12.isBefore(date2)) {
					e.add(tasks.get(j));
				}
			}
			maplist.put(i, e);
		}

		return maplist;
	}

	// Start a case: Inject the process
	// Create a case from the deployed process
	public void StartCase(String tokenCSRF, String processId) {
		String apiUri = "http://localhost:8080/bonita/API/bpm/case/";
		String payloadAsString = "{\"processDefinitionId\": " + processId + "}";
		//System.out.println("The uri is " + apiUri + " the processId " + payloadAsString + " the token " + tokenCSRF);
		HttpResponse response = executePostRequest(apiUri, payloadAsString, tokenCSRF);
		ensureStatusOk(response);

	}

	public HttpResponse executePostRequest(String apiUri, String payloadAsString, String tokenCSRF) {
		//System.out.println("The uri is " + apiUri + " thee processId " + payloadAsString+" token"+tokenCSRF);
		try {
			HttpPost postRequest = new HttpPost(apiUri);
			StringEntity input = new StringEntity(payloadAsString);
			postRequest.setEntity(input);
			postRequest.addHeader("Accept", "application/json");
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.addHeader("X-Bonita-API-Token", tokenCSRF);
			HttpResponse response = httpClient.execute(postRequest, httpContext);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new RuntimeException();
		}

	}

	public TaskInfo bigestscheduleTime(ArrayList<TaskInfo> tasks) throws java.text.ParseException {
		long big = 0;
		int index = 0;
		for (int i = 0; i < tasks.size(); i++) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = dateFormat.parse(tasks.get(i).assigneddate);
			if (big < parsedDate.getTime()) {
				big = parsedDate.getTime();
				index = i;
			}
		}
		//System.out.println("For the LIFO  is " + tasks.get(index));

		return tasks.get(index);

	}

	public TaskInfo smallestscheduleTime(ArrayList<TaskInfo> tasks) throws java.text.ParseException {
		long small = 1614932732;
		int index = 0;
		for (int i = 0; i < tasks.size(); i++) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = dateFormat.parse(tasks.get(i).assigneddate);
			if (small > parsedDate.getTime()) {
				small = parsedDate.getTime();
				index = i;
			}
		}
		//System.out.println("For the FIFO  is " + tasks.get(index));

		return tasks.get(index);

	}
	// Write csv1

	// Write CSV2

	// Write csv3

	// Create and Write in the CSV file
	public void writecasearchive(String filename, ArrayList<CaseInfo> cases) {
		String COMMA_DELIMITER = ",";
		String NEW_LINE_SEPARATOR = "\n";
		String FILE_HEADER = "caseid,startdate,enddate,startedby,processName,archivedDate,Duration";
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename);

			// Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());

			// Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
			// Write a new student object list to the CSV file
			for (int i = 0; i < cases.size(); i++) {
				LocalDateTime date1 = null;
				LocalDateTime date2 = null;
				date1 = LocalDateTime.parse(cases.get(i).endDate, formatter);
				date2 = LocalDateTime.parse(cases.get(i).startDate, formatter);
				fileWriter.append(cases.get(i).caseId);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cases.get(i).startDate);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cases.get(i).endDate);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cases.get(i).startedBy);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cases.get(i).processName);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cases.get(i).archiveDate);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(ChronoUnit.SECONDS.between(date1, date2)));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}

			System.out.println("CSV file was created successfully !!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {

			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}

	public void writetaskinfo(String filename, ArrayList<TaskInfo> readyTask, ArrayList<TaskInfo> assignTask,
			ArrayList<TaskInfo> archiveTask) {
		String COMMA_DELIMITER = ",";
		String NEW_LINE_SEPARATOR = "\n";
		String FILE_HEADER = "caseid,eventid,taskName,state,timestamp,resource";
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename);

			// Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());

			// Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			// Write a new student object list to the CSV file
			for (int i = 0; i < readyTask.size(); i++) {
				fileWriter.append(String.valueOf(readyTask.get(i).rootcaseId));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(readyTask.get(i).id));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(readyTask.get(i).name);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(readyTask.get(i).state);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(readyTask.get(i).timestamp);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(readyTask.get(i).executedBy);
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (int i = 0; i < assignTask.size(); i++) {
				fileWriter.append(String.valueOf(assignTask.get(i).rootcaseId));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(assignTask.get(i).id));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(assignTask.get(i).name);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(assignTask.get(i).state);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(assignTask.get(i).assigneddate);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(assignTask.get(i).executedBy);
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (int i = 0; i < archiveTask.size(); i++) {
				fileWriter.append(String.valueOf(archiveTask.get(i).rootcaseId));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(archiveTask.get(i).id));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(archiveTask.get(i).name);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(archiveTask.get(i).state);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(archiveTask.get(i).timestamp);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(archiveTask.get(i).executedBy);
				fileWriter.append(NEW_LINE_SEPARATOR);
			}

			System.out.println("CSV file was created successfully !!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {

			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}

	public void writestarttask(String filename, ArrayList<TaskInfo> start) {
		String COMMA_DELIMITER = ",";
		String NEW_LINE_SEPARATOR = "\n";
		String FILE_HEADER = "caseid,eventid,taskName,state,timestamp,resource";
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename);

			// Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());

			// Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			// Write a new student object list to the CSV file
			for (int i = 0; i < start.size(); i++) {
				fileWriter.append(start.get(i).rootcaseId);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(start.get(i).id));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(start.get(i).name);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(start.get(i).state);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(start.get(i).timestamp);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(start.get(i).executedBy);
				fileWriter.append(NEW_LINE_SEPARATOR);
			}

			System.out.println("CSV file was created successfully !!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {

			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}

	// create users
	public ArrayList<String> getUserID(String tokencsrf, String bonitaURI, int nbusers) throws HttpException,
			ClientProtocolException, IOException, ParseException, org.json.simple.parser.ParseException {
		ArrayList<String> idsarray = new ArrayList<String>();
		for (int i = 0; i < nbusers; i++) {
			HttpPost postRequest = new HttpPost(bonitaURI + "/API/identity/user/");

			postRequest.addHeader("Accept", "application/json");
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.addHeader("X-Bonita-API-Token", tokencsrf);
			String payload = "{" + "\"userName\": \"test" + i + "\", " + "\"password\": \"test" + i + "\", "
					+ "\"password_confirm\": \"test" + i + "\", " + "\"icon\": \"\", " + "\"firstname\": \"user" + i
					+ "\", " + "\"lastname\": \"user" + i + "\", " + "\"title\": \"Mr\", "
					+ "\"job_title\": \"Technicien\"," + "\"enabled\": \"true\"" + "}";
			StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
			postRequest.setEntity(entity);
			HttpResponse response = httpClient.execute(postRequest, httpContext);

			// System.out.print(EntityUtils.toString(response.getEntity()));
			String Data = EntityUtils.toString(response.getEntity()); // reading the string value
			JSONObject json = (JSONObject) new JSONParser().parse(Data);
			String x = (String) json.get("id");
			idsarray.add(x);
			//System.out.println("The id of client is " + x);
			ensureStatusOk(response);
		}

		return idsarray;
	}

	public void CreateUserProfile(String tokenCSRF, String bonitaURI, ArrayList<String> ids)
			throws ClientProtocolException, HttpException, IOException, ParseException,
			org.json.simple.parser.ParseException {

		for (int i = 0; i < ids.size(); i++) {
			HttpPost postRequest = new HttpPost(bonitaURI + "/API/userXP/profileMember/");

			postRequest.addHeader("Accept", "application/json");
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.addHeader("X-Bonita-API-Token", tokenCSRF);
			String payload = "{" + "\"profile_id\": \"1\", " + "\"member_type\": \"USER\", " + "\"user_id\": \""
					+ ids.get(i) + "\"" + "}";
			StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
			postRequest.setEntity(entity);
			HttpResponse response = httpClient.execute(postRequest, httpContext);
			ensureStatusOk(response);
		}

	}
	
	
	
	// Upload Process in the form of a bar file

	public String UploadyBarFile(String tokenCSRF, String bonitaURI ) throws ClientProtocolException, IOException {
		
		File file = new File("Pool--1.0.bar");
		FileBody barfile= new FileBody(file, ContentType.DEFAULT_BINARY);
		System.out.println("--------------Start uploading the process--------------- "+bonitaURI);
		HttpPost postRequest = new HttpPost(bonitaURI + "/portal/processUpload");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
	
		builder.addPart("file", barfile);
		postRequest.setHeader("Accept", "application/json");
		HttpEntity entity = builder.build();
		postRequest.setEntity(entity);
		HttpResponse response = httpClient.execute(postRequest, httpContext);
		ensureStatusOk(response);
		return  extractFilePathfromResponse(response);
		 
	}
	// extract uploaded File path from response

	public String extractFilePathfromResponse(HttpResponse response) throws org.apache.http.ParseException, IOException {
		
			
			return EntityUtils.toString(response.getEntity());
		
	}

	// Return the Id of the installed Process
	public long installProcessFromBarFile(String uploadedFilePath, String tokenCSRF) {
		String payloadAsString = "{\"fileupload\":\"" + uploadedFilePath + "\"}";
		return extractProcessId(executePostRequest("http://localhost:8080/bonita/API/bpm/process", payloadAsString, tokenCSRF));
	}

	// Extract the process Id 
	public long extractProcessId(HttpResponse response) {
		ensureStatusOk(response);
		try {
			String processJson = EntityUtils.toString(response.getEntity());
			String remain = processJson.substring(processJson.indexOf("id\":") + 5);
			String id = remain.substring(0, remain.indexOf("\""));

			return Long.parseLong(id);
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}

	}
	
	//Enable the process
		public void enableProcess(long processId, String tokenCSRF) {
			System.out.println("Enable the process "+processId+"   "+tokenCSRF);
			String payLoadAsString = "{\"activationState\":\"ENABLED\"}";
			HttpResponse response = executePutRequest("http://localhost:8080/bonita/API/bpm/process/"+processId, payLoadAsString, tokenCSRF);
			ensureStatusOk(response);
			
			
		}

}
