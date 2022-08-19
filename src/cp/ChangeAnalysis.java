package cp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utils.GitUtil;
import utils.MyFileUtils;

public class ChangeAnalysis {
	private String cwd;
	private String repo;
	private HashMap<String, HashMap<String, Integer>> df;

	public ChangeAnalysis(String repo, String cwd) {
		this.repo = repo;
		this.cwd = cwd;
		this.df = new HashMap<>();
	}

	public void analyse(String base, String head, String project) throws IOException {

		String[] commits = GitUtil.getCommits(base, head, repo);
		System.out.println("Commits: " + commits.length);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Project,Revision(prev),Revision(cur),Added,Deleted,File, Total\n");

		String[] changes = GitUtil.getChanges(base, head, repo);
		System.out.println("Number of changed files: "+ changes.length);
		for (String change : changes) {
			
			if(change.split("\t").length < 3) continue;
			
			String added = change.split("\t")[0];
			String deleted = change.split("\t")[1];
			String file = change.split("\t")[2];
			
			int total = 0;
			
			try {
				total = Integer.parseInt(added) + Integer.parseInt(deleted);				
			} catch (NumberFormatException e) {
				// TODO: handle exception
				System.err.println(e);
				System.err.println("Ignoring the following change:");
				System.err.println(change);
				continue;
			}
			
			stringBuilder.append(project + "," + base + "," + head + "," + change.replace('\t', ',') + "," + total);
			stringBuilder.append(System.lineSeparator());
			
			if (df.containsKey(file)) {
				HashMap<String, Integer> tempMap = df.get(file);
				tempMap.put(head, total);
				df.put(file, tempMap);
			} else {
				HashMap<String, Integer> tempMap = new HashMap<>();
				tempMap.put(head, total);
				df.put(file, tempMap);
			}
			
		}

		MyFileUtils.writeToFile(new File(cwd + File.separator + "changes-" + base.replace('/', '-')  + "--" + head.replace('/', '-')  + ".csv"), stringBuilder.toString());

	}

	public void run(Map<String, Object> configmap) throws IOException {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		ArrayList<String> revisions = (ArrayList<String>) configmap.get("revisions");
		String project = (String) configmap.get("project");
		for (int i = 0; i < revisions.size()-1; i++) {
			String base = (String) revisions.get(i);
			String head = (String) revisions.get(i+1);
			System.out.println("Analysing " + base + ".." + head);
			analyse(base, head, project);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Class");
		
		for (int i = 1; i < revisions.size(); i++) {
			stringBuilder.append(",");
			stringBuilder.append(revisions.get(i));
		}
		
		stringBuilder.append(System.lineSeparator());
		
		for (Map.Entry<String, HashMap<String, Integer>> mapElement : df.entrySet()) {
			
			String file = mapElement.getKey();
			
			stringBuilder.append(file);
			
			HashMap<String, Integer> tempMap = mapElement.getValue();
			
			for (int i = 1; i < revisions.size(); i++) {
				String string = revisions.get(i);
				if (tempMap.containsKey(string)) {
					stringBuilder.append(",");
					stringBuilder.append(tempMap.get(string));
					
				} else {
					stringBuilder.append(",");
					stringBuilder.append(0);
				}
			}
			
			stringBuilder.append(System.lineSeparator());
			
		}
		
		MyFileUtils.writeToFile(new File(cwd + File.separator + project + "-cp" + ".csv"), stringBuilder.toString());

	}
}
