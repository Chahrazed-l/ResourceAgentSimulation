package a;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import org.apache.batik.parser.PathArrayProducer;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import org.apache.http.protocol.HttpContext;

import com.resource.start.PlatformInfo;
import com.resource.start.Resource;
import com.resource.start.TaskInfo;
import com.resource.start.UserObjectInfo;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

@Agent

public class AgentBehaviour1BDI {

	public CloseableHttpClient httpClient;
	public HttpContext httpContext;
	public PoolingHttpClientConnectionManager conMan;
	public PlatformInfo platformconection;
	@Belief
	protected String uriPlatform = "http://localhost:8080/bonita";
	@AgentFeature
	protected IBDIAgentFeature bdifeature;
	@Belief
	protected String username;
	@Belief
	protected String password;
	@Belief
	protected String url = "/loginservice";
	@Belief
	protected String url1 = "/API/identity/user?f=userName=";
	@Belief
	protected String url2 = "/API/bpm/humanTask?p=0&c=1000&f=state%3dready&f=assigned_id%3d";
	@Belief
	protected String url3 = "/API/bpm/humanTask/";
	@Belief
	protected String url4 = "/API/bpm/activity/";
	@Belief
	protected double param1;
	@Belief
	protected double param2;
	@Belief
	protected double param3;
	@Belief
	protected double batchparam;
	@Belief
	protected double nonbatchparam;
	@Belief
	protected Map<Integer, Vector<Double>> maptrace = new HashMap<Integer, Vector<Double>>();

	protected ArrayList<Double> availability = new ArrayList<Double>();

	protected ArrayList<Integer> instruction = new ArrayList<Integer>();

	protected ArrayList<Double> instructiondur = new ArrayList<Double>();

	protected ArrayList<Double> availabilitydur = new ArrayList<Double>();
	protected long idagent;

	protected ArrayList<TaskInfo> starttasks = new ArrayList<TaskInfo>();
	@Belief
	protected int nbjours;
	@AgentFeature
	IArgumentsResultsFeature args;

	protected ArrayList<TaskInfo> fifolist;

	protected ArrayList<TaskInfo> lifolist;

	protected ArrayList<TaskInfo> randomlist;

	protected double batch = 0;
	protected double nonbatch = 0;
	protected double workingdays = 0;
	protected int totworkingdays = 0;

	protected double fifoperc = 0;
	protected double lifoperc = 0;
	protected double randomperc = 0;
	protected long daydur = 180;
	protected double daydur1 = 180;
	protected long waittime = 0;

	@AgentCreated
	public void init() {
		waittime = daydur * 1000 / 24;
		PlatformInfo platform = new PlatformInfo();
		conMan = platform.getConnectionManager();
		platformconection = new PlatformInfo(HttpClients.custom().setConnectionManager(conMan).build(), uriPlatform);
		fifolist = new ArrayList<TaskInfo>();
		lifolist = new ArrayList<TaskInfo>();
		randomlist = new ArrayList<TaskInfo>();
		UserObjectInfo obj = new UserObjectInfo();
		obj = (UserObjectInfo) args.getArguments().get("object");
		availability.addAll(obj.av);
		instruction.addAll(obj.inst);
		nbjours = obj.nbjour;
		for (int i = 0; i < availability.size(); i++) {
			if (instruction.get(i) != 0) {
				instructiondur.add((daydur * availability.get(i) / (instruction.get(i) * 100.0)));
				availabilitydur.add((daydur * availability.get(i) / 100.0));
				totworkingdays = totworkingdays + 1;
			} else {
				instructiondur.add(0.0);
				availabilitydur.add(0.0);
			}

		}
		Resource r = new Resource();
		r = (Resource) args.getArguments().get("resource");
		param1 = r.fiforate;
		param2 = r.liforate;
		param3 = r.randomrate;
		batchparam = r.batchrate;
		nonbatchparam = r.nonbatchrate;
		username = r.username;
		password = r.password;
		System.out.println("tHE AGENT " + r.username + "  is started ");

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
	public class getActorId {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected long resobj;

		public getActorId(UserObjectInfo obj) {
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
	public class Execute {
		@GoalParameter
		protected UserObjectInfo obj;

		public Execute(UserObjectInfo obj) {
			this.obj = obj;
		}

	}

	@Goal
	public class Preference {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected TaskInfo task;

		public Preference(UserObjectInfo obj) {
			this.obj = obj;
		}

	}

	@Goal
	public class BatchBehav {
		@GoalParameter
		protected UserObjectInfo obj;

		@GoalResult
		protected UserObjectInfo mode;

		public BatchBehav(UserObjectInfo obj) {
			this.obj = obj;
		}
	}

	@Goal
	public class Segmentation {
		@GoalParameter
		protected UserObjectInfo tasks;
		@GoalResult
		protected UserObjectInfo objres;

		public Segmentation(UserObjectInfo tasks) {
			this.tasks = tasks;
		}
	}

	@Plan(trigger = @Trigger(goals = Connect.class))
	public String connectBPMS(UserObjectInfo obj) {
		String token = platformconection.dologin(obj.firststring, obj.secondstring, obj.thirdstring);
		return token;
	}

	@Plan(trigger = @Trigger(goals = getActorId.class))
	public long actorBPMS(UserObjectInfo obj) {
		long id = platformconection.getactorID(obj.firststring, obj.secondstring, obj.thirdstring);
		return id;
	}

	@Plan(trigger = @Trigger(goals = RetrieveTasks.class))
	public ArrayList<TaskInfo> retrivetasks(UserObjectInfo obj) {
		ArrayList<TaskInfo> tasklist = platformconection.retrieveTask(obj.id, obj.token, obj.firststring);
		return tasklist;
	}

	@Plan(trigger = @Trigger(goals = Execute.class))
	public void execute(UserObjectInfo obj) {
		platformconection.executeTask(obj.id, obj.userid, obj.token, obj.firststring);

	}

	@Plan(trigger = @Trigger(goals = Segmentation.class))
	public UserObjectInfo segmentation(UserObjectInfo obj) throws ParseException {
		Map<Integer, Vector<TaskInfo>> maplist = new LinkedHashMap<Integer, Vector<TaskInfo>>();
		boolean mode = true;
		Collections.sort(obj.tasks, platformconection.readydateComparatorFIFO);
		TaskInfo task1 = obj.tasks.get(0);
		TaskInfo task2 = obj.tasks.get(obj.tasks.size() - 1);
		// System.out.println(
		// "The size of tasks is " + obj.tasks.size() + " " + task1.getTimestamp() + " "
		// + task2.getTimestamp());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date1 = sdf.parse(task1.getTimestamp());
		Date date2 = sdf.parse(task2.getTimestamp());

		Date newDate = new Date(date2.getTime() + 1 * 3600 * 1000);
		// System.out.println("The Date is " + date1.toString() + " " +
		// newDate.toString());

		Vector<Vector<String>> vect = platformconection.timeIntervals(date1, newDate);
		maplist = platformconection.listperSlot(vect, obj.tasks);
		UserObjectInfo objres = new UserObjectInfo(maplist, mode);
		return objres;

	}

	@Plan(trigger = @Trigger(goals = Preference.class))
	public TaskInfo preferences(UserObjectInfo obj) throws ParseException {
		TaskInfo task = null;
		if (obj.tasks.size() > 1) {
			if (obj.tasks.size() > 2) {
				if (fifolist.size() == 0 && lifolist.size() == 0 && randomlist.size() == 0) {
					if (obj.param1 == 100.0 || (obj.param1 > obj.param2 && obj.param1 > obj.param3)) {

						task = platformconection.smallestscheduleTime(obj.tasks);
						fifolist.add(task);
					} else if (obj.param2 == 100.0 || (obj.param2 > obj.param1 && obj.param2 > obj.param3)) {

						task = platformconection.bigestscheduleTime(obj.tasks);
						lifolist.add(task);
					} else if (obj.param3 == 100.0 || (obj.param3 > obj.param1 && obj.param3 > obj.param2)) {
						int indextask = new Random().nextInt(obj.tasks.size());
						if (indextask == 0) {
							indextask = indextask + 1;
						} else if (indextask == obj.tasks.size() - 1) {
							indextask = indextask - 1;
						}

						task = obj.tasks.get(indextask);
						randomlist.add(task);
					} else {
						Random randomNum = new Random();
						int r = randomNum.nextInt(3);
						if (r == 0) {

							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else if (r == 1) {

							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);

						} else {
							int indextask = new Random().nextInt(obj.tasks.size());
							if (indextask == 0) {
								indextask = indextask + 1;
							} else if (indextask == obj.tasks.size() - 1) {
								indextask = indextask - 1;
							}

							task = obj.tasks.get(indextask);
							randomlist.add(task);
						}
					}
					obj.tasks.remove(task);

				} else {
					int totalexectasks = fifolist.size() + lifolist.size() + randomlist.size();
					fifoperc = fifolist.size() * 100.0 / totalexectasks;
					lifoperc = lifolist.size() * 100.0 / totalexectasks;
					randomperc = randomlist.size() * 100.0 / totalexectasks;

					if (fifoperc >= obj.param1 && lifoperc >= obj.param2 && randomperc < obj.param3) {
						int indextask = new Random().nextInt(obj.tasks.size());
						if (indextask == 0) {
							indextask = indextask + 1;
						} else if (indextask == obj.tasks.size() - 1) {
							indextask = indextask - 1;
						}

						task = obj.tasks.get(indextask);
						randomlist.add(task);
					} else if (fifoperc >= obj.param1 && lifoperc < obj.param2 && randomperc >= obj.param3) {

						task = platformconection.bigestscheduleTime(obj.tasks);
						lifolist.add(task);

					} else if (fifoperc < obj.param1 && lifoperc >= obj.param2 && randomperc >= obj.param3) {

						task = platformconection.smallestscheduleTime(obj.tasks);
						fifolist.add(task);

					} else if (fifoperc < obj.param1 && lifoperc < obj.param2 && randomperc >= obj.param3) {
						if (obj.param1 - fifoperc > obj.param2 - lifoperc) {
							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else if (obj.param1 - fifoperc < obj.param2 - lifoperc) {
							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);

						} else {
							int p1 = new Random().nextInt(2);
							if (p1 == 0) {

								task = platformconection.bigestscheduleTime(obj.tasks);
								lifolist.add(task);

							} else {

								task = platformconection.smallestscheduleTime(obj.tasks);
								fifolist.add(task);

							}
						}
					} else if (fifoperc < obj.param1 && lifoperc >= obj.param2 && randomperc < obj.param3) {
						if (obj.param1 - fifoperc > obj.param3 - randomperc) {
							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else if (obj.param1 - fifoperc < obj.param3 - randomperc) {
							int indextask = new Random().nextInt(obj.tasks.size());
							if (indextask == 0) {
								indextask = indextask + 1;
							} else if (indextask == obj.tasks.size() - 1) {
								indextask = indextask - 1;
							}

							task = obj.tasks.get(indextask);

							randomlist.add(task);
						} else {
							int p1 = new Random().nextInt(2);
							if (p1 == 0) {
								int indextask = new Random().nextInt(obj.tasks.size());
								if (indextask == 0) {
									indextask = indextask + 1;
								} else if (indextask == obj.tasks.size() - 1) {
									indextask = indextask - 1;
								}

								task = obj.tasks.get(indextask);

								randomlist.add(task);
							} else {

								task = platformconection.smallestscheduleTime(obj.tasks);
								fifolist.add(task);

							}
						}
					} else if (fifoperc >= obj.param1 && lifoperc < obj.param2 && randomperc < obj.param3) {
						if (obj.param2 - lifoperc > obj.param3 - randomperc) {
							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);

						} else if (obj.param2 - lifoperc < obj.param3 - randomperc) {
							int indextask = new Random().nextInt(obj.tasks.size());
							if (indextask == 0) {
								indextask = indextask + 1;
							} else if (indextask == obj.tasks.size() - 1) {
								indextask = indextask - 1;
							}

							task = obj.tasks.get(indextask);

							randomlist.add(task);
						} else {
							int p1 = new Random().nextInt(2);
							if (p1 == 0) {
								int indextask = new Random().nextInt(obj.tasks.size());
								if (indextask == 0) {
									indextask = indextask + 1;
								} else if (indextask == obj.tasks.size() - 1) {
									indextask = indextask - 1;
								}
								task = obj.tasks.get(indextask);

								randomlist.add(task);

							} else {

								task = platformconection.bigestscheduleTime(obj.tasks);
								lifolist.add(task);

							}
						}
					}
					// Tous ils sont egaux aux valeurs voulu mais il reste des taches a executer
					else if (fifoperc == obj.param1 && lifoperc == obj.param2 && randomperc == obj.param3) {
						Random randomNum = new Random();
						int r = randomNum.nextInt(3);
						if (r == 0) {

							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else if (r == 1) {

							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);

						} else {
							int indextask = new Random().nextInt(obj.tasks.size());
							if (indextask == 0) {
								indextask = indextask + 1;
							} else if (indextask == obj.tasks.size() - 1) {
								indextask = indextask - 1;
							}

							task = obj.tasks.get(indextask);
							randomlist.add(task);

						}

					}

					obj.tasks.remove(task);

				}

			}
			// number of tasks is equal to 2
			else {
				if (fifolist.size() == 0 && lifolist.size() == 0) {
					if (obj.param1 == 100.0) {

						task = platformconection.smallestscheduleTime(obj.tasks);
						fifolist.add(task);

					} else if (obj.param2 == 100.0) {

						task = platformconection.bigestscheduleTime(obj.tasks);
						lifolist.add(task);

					} else {
						Random randomNum = new Random();
						int r = randomNum.nextInt(2);
						if (r == 0) {

							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else if (r == 1) {

							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);

						}
					}
					obj.tasks.remove(task);

				}
				// In the middele fifo lifo contain elements
				else {

					int totalexectasks = fifolist.size() + lifolist.size() + randomlist.size();
					fifoperc = fifolist.size() * 100.0 / totalexectasks;
					lifoperc = lifolist.size() * 100.0 / totalexectasks;

					if (fifoperc >= obj.param1 && lifoperc >= obj.param2) {
						if (fifoperc - obj.param1 < lifoperc - obj.param2) {
							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else if (fifoperc - obj.param1 > lifoperc - obj.param2) {
							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);

						} else {
							Random randomNum = new Random();
							int r = randomNum.nextInt(2);
							if (r == 0) {

								task = platformconection.smallestscheduleTime(obj.tasks);
								fifolist.add(task);

							} else if (r == 1) {

								task = platformconection.bigestscheduleTime(obj.tasks);
								lifolist.add(task);

							}

						}
					} else if (fifoperc >= obj.param1 && lifoperc < obj.param2) {

						task = platformconection.bigestscheduleTime(obj.tasks);
						lifolist.add(task);

					} else if (fifoperc < obj.param1 && lifoperc >= obj.param2) {

						task = platformconection.smallestscheduleTime(obj.tasks);
						fifolist.add(task);

					} else if (fifoperc < obj.param1 && lifoperc < obj.param2) {
						if (obj.param1 - fifoperc < obj.param2 - lifoperc) {
							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);
							System.out.println(" GO en LIFO ");
						} else if (obj.param1 - fifoperc > obj.param2 - lifoperc) {
							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else {
							int p1 = new Random().nextInt(2);
							if (p1 == 0) {

								task = platformconection.bigestscheduleTime(obj.tasks);
								lifolist.add(task);

							} else {
								task = platformconection.smallestscheduleTime(obj.tasks);
								fifolist.add(task);

							}
						}
					}
					// Tous ils sont egaux aux valeurs voulu mais il reste des taches a executer
					else {
						Random randomNum = new Random();
						int r = randomNum.nextInt(2);
						if (r == 0) {

							task = platformconection.smallestscheduleTime(obj.tasks);
							fifolist.add(task);

						} else if (r == 1) {

							task = platformconection.bigestscheduleTime(obj.tasks);
							lifolist.add(task);

						}

					}

					obj.tasks.remove(task);

				}
			}

		}
		// If the size is equal to one
		else if (obj.tasks.size() == 1) {		
			task = obj.tasks.get(0);
			obj.nbtask = obj.nbtask - 1;
			obj.tasks.remove(task);
			int totalexectasks = fifolist.size() + lifolist.size() + randomlist.size();
			fifoperc = fifolist.size() * 100.0 / totalexectasks;
			lifoperc = lifolist.size() * 100.0 / totalexectasks;
			randomperc = randomlist.size() * 100.0 / totalexectasks;

		}
		return task;
	}

	@Plan(trigger = @Trigger(goals = BatchBehav.class))
	public UserObjectInfo batchbehav(UserObjectInfo obj) throws ParseException {
		boolean mode = false;
		double batchperc = 0.0;
		double nonbatchperc = 0.0;
		UserObjectInfo objres = new UserObjectInfo();
		if (batch == 0 && nonbatch == 0) {
			if (obj.param1 == 100.0) {
				mode = true;
				batch = batch + 1;
				workingdays = workingdays + 1;
			} else if (obj.param2 == 100.0) {
				mode = false;
				nonbatch = nonbatch + 1;
				workingdays = workingdays + 1;
			} else {
				long r = Math.round(Math.random());

				if (r == 1) {
					// Go non batch: React in time
					mode = false;
					nonbatch = nonbatch + 1;
					workingdays = workingdays + 1;

				} else {
					// Go en batch: take a nap
					mode = true;
					batch = batch + 1;
					workingdays = workingdays + 1;

				}
			}
		} else {
			if (obj.param1 == 100) {
				mode = true;
				batch = batch + 1;
				workingdays = workingdays + 1;
			} else if (obj.param2 == 100) {
				mode = false;
				nonbatch = nonbatch + 1;
				workingdays = workingdays + 1;

			} else {
				batchperc = batch * 100 / workingdays;
				nonbatchperc = nonbatch * 100 / workingdays;
				if (batchperc >= obj.param1 && nonbatchperc < obj.param2) {

					mode = false;
					nonbatch = nonbatch + 1;
					workingdays = workingdays + 1;

				} else if (batchperc < obj.param1 && nonbatchperc >= obj.param2) {

					mode = true;
					batch = batch + 1;
					workingdays = workingdays + 1;

				} else {
					long r = Math.round(Math.random());

					if (r == 1) {
						// Go non batch: React in time

						mode = false;
						nonbatch = nonbatch + 1;
						workingdays = workingdays + 1;

					} else {
						// Go en batch: take a nap

						mode = true;
						batch = batch + 1;
						workingdays = workingdays + 1;

					}
				}
			}
		}
		objres = new UserObjectInfo(mode, obj.tasks, obj.tasks.size());

		return objres;
	}

	@SuppressWarnings("unchecked")
	@AgentBody
	public void MyBehaviour() throws InterruptedException {

		int daycounter = 0;
		long start = 0;
		// Check if he is available to work on the process today
		while (daycounter < availability.size()) {
			start = System.currentTimeMillis();
			if (availability.get(daycounter) != 0) {
				System.out.println(" -------------- > The day " + daycounter + " Agent " + username
						+ " Available to work   " + availability.get(daycounter));
				// Connect and do the work
				UserObjectInfo obj = new UserObjectInfo(username, password, url);
				String token = (String) bdifeature.dispatchTopLevelGoal(new Connect(obj)).get();
				UserObjectInfo obj1 = new UserObjectInfo(username, token, url1);
				long id = (Long) bdifeature.dispatchTopLevelGoal(new getActorId(obj1)).get();
				idagent = id;
				System.out.println("The token is " + token + "The actor id  is  " + id);
				UserObjectInfo obj2 = new UserObjectInfo(id, token, url2);
				ArrayList<TaskInfo> tasks = (ArrayList<TaskInfo>) bdifeature
						.dispatchTopLevelGoal(new RetrieveTasks(obj2)).get();
				System.out.println(" -------------- >  tasks retrieved  " + tasks.size() + " on The day " + daycounter
						+ " by Agent " + username);

				if (instruction.get(daycounter) > 1) {

					System.out.println("The size of the tasks is " + tasks.size() + " on The day " + daycounter
							+ " by Agent " + username);
					ArrayList<TaskInfo> vect = new ArrayList<TaskInfo>(tasks);
					if (vect.size() > 1) {
						UserObjectInfo objbatch = new UserObjectInfo(batchparam, nonbatchparam, vect, totworkingdays);
						UserObjectInfo mode = (UserObjectInfo) bdifeature.dispatchTopLevelGoal(new BatchBehav(objbatch))
								.get();
						if (mode.mode) {
							System.out.println(mode.tasks.size() + "  ------ Work on batch ");
							int taille = instruction.get(daycounter) / 2;
							int reste = instruction.get(daycounter) - taille;
							//double duration1 = taille * instructiondur.get(daycounter);
							//double duration2 = (instruction.get(daycounter) - taille) * instructiondur.get(daycounter);
							boolean oktask1 = false;
							double periode1 = 0;
							while (!oktask1) {
								UserObjectInfo obj5 = new UserObjectInfo(param1, param2, param3, taille, vect);
								TaskInfo task = (TaskInfo) bdifeature.dispatchTopLevelGoal(new Preference(obj5)).get();
								System.out.println("The ID of the Task to be executed " + task.id + " on The day "
										+ daycounter + " by Agent " + username);
								task.setState("START");
								Date currentDate = new Date(System.currentTimeMillis());
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								// System.out.println("Milliseconds to Date: " + df.format(currentDate));
								task.setTimestamp(df.format(currentDate));
								starttasks.add(task);
								Thread.sleep(Math.round(instructiondur.get(daycounter) * 1000));
								periode1 = periode1 + instructiondur.get(daycounter);
								UserObjectInfo obj4 = new UserObjectInfo(task.id, id, token, url4);
								bdifeature.dispatchTopLevelGoal(new Execute(obj4)).get();
								System.out.println("The size of the tasks " + taille + " on The day " + daycounter
										+ " by Agent " + username);
								taille = taille - 1;
								if (taille == 0) {
									oktask1 = true;
								}

							}

							System.out.println(" Finish My first part of work I will take a break" + " on The day "
									+ daycounter + " by Agent " + username);
							
							Thread.sleep(waittime);
							boolean oktask2 = false;
							double periode2 = 0;
							while (!oktask2) {
								UserObjectInfo obj5 = new UserObjectInfo(param1, param2, param3, reste, vect);
								TaskInfo task = (TaskInfo) bdifeature.dispatchTopLevelGoal(new Preference(obj5)).get();
								System.out.println("The ID of the Task to be executed " + task.id + " on The day "
										+ daycounter + " by Agent " + username);
								task.setState("START");
								Date currentDate = new Date(System.currentTimeMillis());
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								// System.out.println("Milliseconds to Date: " + df.format(currentDate));
								task.setTimestamp(df.format(currentDate));
								starttasks.add(task);
								Thread.sleep(Math.round(instructiondur.get(daycounter) * 1000));
								UserObjectInfo obj4 = new UserObjectInfo(task.id, id, token, url4);
								periode2 = periode2 + instructiondur.get(daycounter);
								bdifeature.dispatchTopLevelGoal(new Execute(obj4)).get();
								System.out.println("The size of the tesks " + reste + " on The day " + daycounter
										+ " by Agent " + username);
								reste = reste - 1;
								if (reste == 0) {
									oktask2 = true;
								}
							}
						} else {
							// Non Batch
							System.out.println("----- Work on  non batch and the size is " + vect.size()
									+ " on The day " + daycounter + " by Agent " + username);
							boolean ok = false;
							int nbtask = instruction.get(daycounter);
							double periode = 0;
							while (!ok) {
								UserObjectInfo obj5 = new UserObjectInfo(param1, param2, param3, nbtask, vect);
								TaskInfo task = (TaskInfo) bdifeature.dispatchTopLevelGoal(new Preference(obj5)).get();
								System.out.println("The ID of the Task to be executed " + task.id + " on The day "
										+ daycounter + " by Agent " + username);
								task.setState("START");
								Date currentDate = new Date(System.currentTimeMillis());
								DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
								// System.out.println("Milliseconds to Date: " + df.format(currentDate));
								task.setTimestamp(df.format(currentDate));
								starttasks.add(task);
								Thread.sleep(Math.round(instructiondur.get(daycounter) * 1000));
								UserObjectInfo obj4 = new UserObjectInfo(task.id, id, token, url4);
								periode = periode + instructiondur.get(daycounter);
								bdifeature.dispatchTopLevelGoal(new Execute(obj4)).get();
								System.out.println("The size of the tesks " + nbtask + " on The day " + daycounter
										+ " by Agent " + username);
								nbtask = nbtask - 1;
								if (nbtask == 0) {
									ok = true;
								}

							}
						}
					}

				} else if (instruction.get(daycounter) == 1) {
					System.out.println(
							" We do have one task for the Day : No batch behav is detected but preference in queuing can be detected! "
									+ tasks.get(0).id);
					UserObjectInfo obj5 = new UserObjectInfo(param1, param2, param3, tasks.size(), tasks);
					TaskInfo task = (TaskInfo) bdifeature.dispatchTopLevelGoal(new Preference(obj5)).get();
					System.out.println("The ID of the Task to be executed " + task.id);
					task.setState("START");
					Date currentDate = new Date(System.currentTimeMillis());
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					// System.out.println("Milliseconds to Date: " + df.format(currentDate));
					task.setTimestamp(df.format(currentDate));
					starttasks.add(task);
					Thread.sleep(Math.round(instructiondur.get(daycounter) * 1000));
					UserObjectInfo obj4 = new UserObjectInfo(task.id, id, token, url4);
					bdifeature.dispatchTopLevelGoal(new Execute(obj4)).get();
				}

			}
			// The resource is not available to work
			else {
				System.out.println(" -------------- >  Not Available to work  today " + availability.get(daycounter)
						+ " on The day " + daycounter + " by Agent " + username);
			}
			long end = System.currentTimeMillis();
			daycounter++;
			long period = (daydur * 1000) - (end - start);
			System.out.println(period);
			Thread.sleep(period);
		}

		System.out.println("Fifo " + fifoperc + " Lifo " + lifoperc + " Random " + randomperc);
		System.out.println("Batch " + batch + "  Nonbatch" + nonbatch);
		platformconection.writestarttask("taskstartt" + idagent + ".csv", starttasks);

	}

}
