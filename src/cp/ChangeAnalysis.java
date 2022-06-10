package cp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import utils.GitUtil;
import utils.MyFileUtils;

public class ChangeAnalysis {
	private String cwd;
	private String repo;

	public ChangeAnalysis(String repo, String cwd) {
		this.repo = repo;
		this.cwd = cwd;
	}

	public void analyse(String base, String head, String project) throws IOException {

		String[] commits = GitUtil.getCommits(base, head, repo);
		System.out.println("Commits: " + commits.length);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Project\tRevision(prev)\tRevision(cur)\tAdded\tDeleted\tFile\n");

		String[] changes = GitUtil.getChanges(base, head, repo);
		System.out.println("Number of changed files: "+ changes.length);
		for (String change : changes) {
			stringBuilder.append(project + "\t" + base + "\t" + head + "\t" + change);
			stringBuilder.append(System.lineSeparator());
		}

		MyFileUtils.writeToFile(new File(cwd + File.separator + "changes-" + base  + "--" + head  + ".csv"), stringBuilder.toString());

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
	}
}
