package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import cp.ChangeAnalysis;

public class Main {
	public static void main(String[] args) {

		String cwd = "demo-project";
		String project = "deeplearning4j";

		String projectConfigFile = cwd + File.separator + project + ".conf";
		String repo = cwd + File.separator + project + "-repo";
		String resDir = cwd + File.separator + "res" + File.separator + project;

		File dir = new File(resDir);
		if (!dir.exists())
			dir.mkdirs();

		try {
			Yaml projectConfigYaml = new Yaml();
			Reader projectFileReader = new FileReader(projectConfigFile);
			Map<String, Object> configmap = projectConfigYaml.load(projectFileReader);
			System.out.println(configmap.get("revisions"));

			dir = new File(resDir + File.separator);
			if (!dir.exists())
				dir.mkdir();

			ChangeAnalysis changeAnalysis = new ChangeAnalysis(repo, dir.getAbsolutePath());
			changeAnalysis.run(configmap);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
