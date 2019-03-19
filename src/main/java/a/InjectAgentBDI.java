package a;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import com.resource.start.CaseInfo;
import com.resource.start.PlatformInfo;
import com.resource.start.Resource;
import com.resource.start.TaskInfo;
import com.resource.start.UserObjectInfo;

import a.AgentBehaviour1BDI.RetrieveTasks;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

@Agent
public class InjectAgentBDI {
	public CloseableHttpClient httpClient;
	public HttpContext httpContext;
	public PoolingHttpClientConnectionManager conMan;
	public PlatformInfo platformconection;
	@Belief
	protected String uriPlatform = "http://localhost:8080/bonita";
	@Belief
	protected String archivetaskuri = "/API/bpm/archivedHumanTask?p=0&c=7000&f=assigned_id%3d";
	@Belief
	protected String archivecaseuri = "/API/bpm/archivedCase?p=0&c=7000";
	@Belief
	protected String url2 = "/API/bpm/humanTask?p=0&c=1000&f=state%3dready&f=user_id%3d";
	@Belief
	protected String url3 = "/API/bpm/humanTask/";
	@Belief
	protected String urlusers = "/API/identity/user?p=0&c=50&o=lastname%20ASC&s=user&f=enabled%3dtrue";
	@AgentFeature
	protected IBDIAgentFeature bdifeature;
	@Belief
	protected String username = "test";
	@Belief
	protected String password = "test";
	@Belief
	protected String url = "/loginservice";
	@Belief
	protected String processId = "8837039536069969213";
	@Belief
	protected long timerest = 1000;
	@Belief
	protected long daydur = 180;
	@Belief
	protected int nbusers = 20;
	@AgentFeature
	IArgumentsResultsFeature args;
	protected Map<Integer, ArrayList<Resource>> resources = new HashMap<Integer, ArrayList<Resource>>();
	protected ArrayList<TaskInfo> readyTasks = new ArrayList<TaskInfo>();

	protected ArrayList<TaskInfo> assignedTasks = new ArrayList<TaskInfo>();

	protected ArrayList<TaskInfo> archivedTasks = new ArrayList<TaskInfo>();
	protected ArrayList<Integer> instruction = new ArrayList<Integer>();

	@AgentCreated
	public void init() {
		PlatformInfo platform = new PlatformInfo();
		conMan = platform.getConnectionManager();
		platformconection = new PlatformInfo(HttpClients.custom().setConnectionManager(conMan).build(), uriPlatform);
		UserObjectInfo obj = new UserObjectInfo();
		resources = (Map<Integer, ArrayList<Resource>>) args.getArguments().get("map");

	}

	@Goal
	public class Connect {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected String token;

		public Connect(UserObjectInfo obj) {
			this.obj = obj;

		}

	}

	@Goal
	public class InstallProcess {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected long processid;

		public InstallProcess(UserObjectInfo obj) {
			this.obj = obj;

		}

	}

	@Goal
	public class StartCase {
		@GoalParameter
		protected UserObjectInfo obj;

		public StartCase(UserObjectInfo obj) {
			this.obj = obj;

		}

	}

	@Goal
	public class AutoAssign {
		@GoalParameter
		protected UserObjectInfo obj;

		public AutoAssign(UserObjectInfo obj) {
			this.obj = obj;
		}

	}

	@Goal
	public class RetrieveTasks {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected ArrayList<TaskInfo> tasklist;

		public RetrieveTasks(UserObjectInfo obj) {
			this.obj = obj;

		}

	}

	@Goal
	public class Retrieveusers {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected ArrayList<Resource> userlist;

		public Retrieveusers(UserObjectInfo obj) {
			this.obj = obj;

		}

	}

	@Goal
	public class Createusers {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected ArrayList<String> tasklist;

		public Createusers(UserObjectInfo obj) {
			this.obj = obj;

		}

	}

	@Plan(trigger = @Trigger(goals = Connect.class))
	public String connectBPMS(UserObjectInfo obj) {
		String token = platformconection.dologin(obj.firststring, obj.secondstring, obj.thirdstring);
		return token;
	}

	@Plan(trigger = @Trigger(goals = InstallProcess.class))
	public long installproc(UserObjectInfo obj) throws ClientProtocolException, IOException {
		// Upload process and extract path
		//System.out.println("");
		String uploadedFilePath = platformconection.UploadyBarFile(obj.token, obj.idprocess);
		// install process
		long extractProcessId = platformconection.installProcessFromBarFile(uploadedFilePath, obj.token);
		// enable the process
		platformconection.enableProcess(extractProcessId, obj.token);
		return extractProcessId;
	}

	@Plan(trigger = @Trigger(goals = StartCase.class))
	public void startcase(UserObjectInfo obj) {
		//System.out.println("The token " + obj.getToken() + " process " + obj.getIdprocess());
		platformconection.StartCase(obj.getToken(), obj.getIdprocess());
	}

	@Plan(trigger = @Trigger(goals = AutoAssign.class))
	public void autoassign(UserObjectInfo obj) {
		platformconection.autoAssign(obj.id, obj.userid, obj.token, obj.firststring);

	}

	@Plan(trigger = @Trigger(goals = RetrieveTasks.class))
	public ArrayList<TaskInfo> retrivetasks(UserObjectInfo obj) {
		ArrayList<TaskInfo> tasklist = platformconection.retrieveTask(obj.id, obj.token, obj.firststring);
		return tasklist;
	}

	@Plan(trigger = @Trigger(goals = Createusers.class))
	public ArrayList<String> createuser(UserObjectInfo obj) {
		ArrayList<String> ids = new ArrayList<String>();
		try {
			ids = platformconection.getUserID(obj.firststring, obj.secondstring, obj.nbjour);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			platformconection.CreateUserProfile(obj.firststring, obj.secondstring, ids);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ids;
	}

	@SuppressWarnings("unchecked")
	@AgentBody
	public void injectprocess() throws InterruptedException {
		int days = 1;
		long start = 0;
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		Date currentDate = new Date(System.currentTimeMillis());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String datesater = df.format(currentDate);
		//UserObjectInfo objconect1 = new UserObjectInfo(username, password, url);
		//String token1 = (String) bdifeature.dispatchTopLevelGoal(new Connect(objconect1)).get();
		//System.out.println("The token " + token1);
		//UserObjectInfo o= new UserObjectInfo(token1, uriPlatform, nbusers);
		//bdifeature.dispatchTopLevelGoal(new Createusers(o)).get();
		while (days < resources.size()) {
			System.out.println("Start the day "+days);
			start = System.currentTimeMillis();
			// Connect
			UserObjectInfo objconect = new UserObjectInfo(username, password, url);
			String token = (String) bdifeature.dispatchTopLevelGoal(new Connect(objconect)).get();
			ArrayList<Resource> currentresource = new ArrayList<Resource>();
			currentresource=resources.get(days);
			//System.out.println("------"+currentresource.size());
			for (int i = 0; i < currentresource.size(); i++) {
				UserObjectInfo objinject = new UserObjectInfo(token, processId);
				for (int j = 0; j < currentresource.get(i).nbtask; j++) {
					bdifeature.dispatchTopLevelGoal(new StartCase(objinject)).get();
					Thread.sleep(100);

				}
				UserObjectInfo obj2 = new UserObjectInfo(currentresource.get(i).id, token, url2);
				tasks = (ArrayList<TaskInfo>) bdifeature.dispatchTopLevelGoal(new RetrieveTasks(obj2)).get();
				ArrayList<TaskInfo> taskassign = new ArrayList<TaskInfo>();
				taskassign.addAll(tasks);
				for (int c = 0; c < tasks.size(); c++) {
					//System.out.println(tasks.get(c).id);
					int k = 0;
					boolean found = false;
					while (!found && k < readyTasks.size()) {
						if (readyTasks.get(k).id == tasks.get(c).id) {
							found = true;
						}
						k++;
					}
					if (!found) {
						readyTasks.add(tasks.get(c));
					}
				}
				for (int c = 0; c < tasks.size(); c++) {
					UserObjectInfo obj3 = new UserObjectInfo(tasks.get(c).id, currentresource.get(i).id, token, url3);
					bdifeature.dispatchTopLevelGoal(new AutoAssign(obj3)).get();
					//System.out.println("Task AutoASSIGNED");
				}
				tasks = (ArrayList<TaskInfo>) bdifeature.dispatchTopLevelGoal(new RetrieveTasks(obj2)).get();
				for (int c = 0; c < tasks.size(); c++) {
					int k = 0;
					boolean found = false;
					while (!found && k < assignedTasks.size()) {
						if (assignedTasks.get(k).id == tasks.get(c).id) {
							found = true;
						}
						k++;
					}
					if (!found) {
						tasks.get(c).setState("SCHEDULE");
						assignedTasks.add(tasks.get(c));
					}
				}
			}

			long end = System.currentTimeMillis();
			long injtime = end - start;
			System.out.println("The inject time "+injtime);
			long period = (daydur * 1000) - (end - start);
			days++;
			// Sleep for the rest of the day
			Thread.sleep(period);
		}
		 Thread.sleep(60000);
		Date currentDate1 = new Date(System.currentTimeMillis());
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String dateend = df.format(currentDate1);
		// Interrogate the base to get all the archive tasks and cases
		// Connect
		UserObjectInfo objconect = new UserObjectInfo(username, password, url);
		String token = (String) bdifeature.dispatchTopLevelGoal(new Connect(objconect)).get();
		//System.out.println("The token " + token);
		// Start cases
		//System.out.println("The token is " + token);
		try {
			ArrayList<TaskInfo> tas= new ArrayList<TaskInfo>();
			for(int i=0; i<resources.get(1).size();i++) {
				 tas= platformconection.archivedTask(token,resources.get(1).get(i).id, archivetaskuri);
			}
		
			for (int i = 0; i < tas.size(); i++) {
				tas.get(i).setState("COMPLETE");
			}
			archivedTasks.addAll(tas);

			ArrayList<CaseInfo> cases = platformconection.archivedcases(token, archivecaseuri);
			//System.out.println("The size is --------------- " + cases.size());
			// Write results in the csv files
			platformconection.writetaskinfo("taskinfo.csv", readyTasks, assignedTasks, archivedTasks);
			platformconection.writecasearchive("caseinfo.csv", cases);
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
		System.out.println("Start Date " + datesater + " end date " + dateend);

	}

}
