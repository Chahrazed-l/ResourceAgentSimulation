package com.resource.start;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

public class Start {
	ArrayList<Resource> resourceList = new ArrayList<Resource>();
	static String fileIn = "";
	static String line = null;
	static int nbagent = 20;
	static UserObjectInfo obj = new UserObjectInfo();

	public static void main(String[] args) throws IOException, InterruptedException {

		// TODO Auto-generated method stub
		PlatformConfiguration platformConfig = PlatformConfiguration.getDefaultNoGui();
		IExternalAccess platform = Starter.createPlatform(platformConfig).get();
		IFuture<IComponentManagementService> fut = SServiceProvider.getService(platform,
				IComponentManagementService.class);
		IComponentManagementService cms = fut.get();
		CreationInfo agentparams1 = new CreationInfo();
		Map<Integer, ArrayList<Resource>> rmap = readcsv3("configuration/configinject.csv");
		agentparams1.addArgument("map", rmap);
		ITuple2Future<IComponentIdentifier, Map<String, Object>> fut3 = cms.createComponent("a.InjectAgentBDI.class",
			agentparams1);
		Thread.sleep(70000);
		ArrayList<Resource> r = readcsv("configuration/config.csv");
		for (int i = 0; i < nbagent; i++) {
			CreationInfo agentparams = new CreationInfo();
			fileIn = "configuration/test" + i + ".csv";
			// read about availability and nbinstruction
			obj = readcsv2(fileIn);
			// add first argument
			agentparams.addArgument("object", obj);
			// add second argument
			agentparams.addArgument("resource", r.get(i));
			// Launch the agent
			ITuple2Future<IComponentIdentifier, Map<String, Object>> fut2 = cms.createComponent("myAgent" + i,
					"a.AgentBehaviour1BDI.class", agentparams);

		}

	}

	public static Map<Integer, ArrayList<Resource>> readcsv3(String filename)
			throws NumberFormatException, IOException {
		Map<Integer, ArrayList<Resource>> resources = new HashMap<Integer, ArrayList<Resource>>();
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while ((line = bufferedReader.readLine()) != null) {
			String[] temp = line.split(";");
			int i = Integer.parseInt(temp[0]);
			String resourcename = temp[1];
			String username = temp[2];
			int nbinstruction = Integer.parseInt(temp[3]);
			long id = Long.parseLong(temp[4]);
			Resource resource = new Resource(resourcename, username, nbinstruction, id);

			if (!resources.containsKey(i)) {
				ArrayList<Resource> r = new ArrayList<Resource>();
				r.add(resource);
				resources.put(i, r);
			} else {
				resources.get(i).add(resource);
			}
		}

		return resources;
	}

	public static ArrayList<Resource> readcsv(String filename) throws IOException {
		ArrayList<Resource> resource = new ArrayList<Resource>();
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		while ((line = bufferedReader.readLine()) != null) {
			String[] temp = line.split(";");
			String username = temp[0];
			String password = temp[1];
			double batchrate = Double.parseDouble(temp[2]);
			double nonbatchrate = Double.parseDouble(temp[3]);
			double fiforate = Double.parseDouble(temp[4]);
			double liforate = Double.parseDouble(temp[5]);
			double randomrate = Double.parseDouble(temp[6]);
			Resource r = new Resource(username, password, batchrate, nonbatchrate, fiforate, liforate, randomrate);
			resource.add(r);
		}
		bufferedReader.close();
		return resource;
	}

	public static UserObjectInfo readcsv2(String filename) throws IOException {

		ArrayList<Double> av = new ArrayList<Double>();
		ArrayList<Integer> inst = new ArrayList<Integer>();
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while ((line = bufferedReader.readLine()) != null) {
			String[] temp = line.split(";");
			av.add(Double.parseDouble(temp[0]));
			inst.add(Integer.parseInt(temp[1]));
		}
		bufferedReader.close();
		UserObjectInfo obj = new UserObjectInfo(av, inst, av.size());
		return obj;
	}
}
