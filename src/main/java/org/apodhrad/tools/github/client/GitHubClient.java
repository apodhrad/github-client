package org.apodhrad.tools.github.client;

import java.io.IOException;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.StringsCompleter;

import org.apodhrad.tools.github.client.rest.Issue;
import org.apodhrad.tools.github.client.rest.Pull;
import org.apodhrad.tools.github.client.rest.RESTClient;

import com.google.gson.Gson;

/**
 * 
 * @author Andrej Podhradsky (andrej.podhradsky@gmail.com)
 *
 */
public class GitHubClient {

	public static void main(String[] args) {
		String owner = "null";
		String repo = "null";
		try {
			ConsoleReader console = new ConsoleReader();
			console.addCompleter(new ArgumentCompleter(new StringsCompleter("repo", "issue",
					"pull", "exit", "clear", "help"), new StringsCompleter("use", "create", "list")));
			String line = null;
			while ((line = console.readLine("github:" + owner + "/" + repo + "> ")) != null) {
				if (line.trim().equals("repo use")) {
					owner = console.readLine("Owner: ");
					repo = console.readLine("Repo: ");
				} else if (line.trim().startsWith("issue list")) {
					String url = "https://api.github.com/repos/" + owner + "/" + repo + "/issues";
					String[] params = line.trim().split(" ");
					int number = 0;
					if (params.length > 2) {
						number = Integer.valueOf(params[2]);
					}
					if (number > 0) {
						url += "/" + number;
						String json = RESTClient.get(url);
						Issue issue = new Gson().fromJson(json, Issue.class);
						console.println(issue.toString());
					} else {
						String json = RESTClient.get(url);
						Issue[] issues = new Gson().fromJson(json, Issue[].class);
						for (int i = 0; i < issues.length; i++) {
							console.println(issues[i].toString());
						}
					}
				} else if (line.trim().equals("issue create")) {
					String url = "https://api.github.com/repos/" + owner + "/" + repo + "/issues";
					String title = console.readLine("Title: ");
					String body = console.readLine("Body: ");
					String user = console.readLine("User: ");
					String password = console.readLine("Password: ", new Character('*'));
					Issue issue = new Issue();
					issue.setTitle(title);
					issue.setBody(body);
					RESTClient.post(url, new Gson().toJson(issue), user, password);
					console.println("New issues created");
				}
				/*
				 * else if (line.trim().equals("pull list")) { String url =
				 * "https://api.github.com/repos/" + owner + "/" + repo +
				 * "/pulls"; String[] params = line.trim().split(" "); int
				 * number = 0; if (params.length > 2) { number =
				 * Integer.valueOf(params[2]); } if (number > 0) { url += "/" +
				 * number; String json = RESTClient.get(url); Pull pull = new
				 * Gson().fromJson(json, Pull.class);
				 * console.println(pull.toString()); } else { String json =
				 * RESTClient.get(url); Pull[] pulls = new Gson().fromJson(json,
				 * Pull[].class); for (int i = 0; i < pulls.length; i++) {
				 * console.println(pulls[i].toString()); } } }
				 */
				else if (line.trim().equals("pull create")) {
					String url = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls";
					String number = console.readLine("Issues number: ");
					String head = console.readLine("Head: ");
					String base = "master";
					String user = console.readLine("User: ");
					String password = console.readLine("Password: ", new Character('*'));
					Pull pull = new Pull();
					pull.setIssue(Integer.valueOf(number));
					pull.setHead(head);
					pull.setBase(base);
					String json = new Gson().toJson(pull);
					String result = RESTClient.post(url, json, user, password);
					console.println(result);
				} else if (line.trim().equals("exit")) {
					break;
				} else if (line.trim().equals("help")) {
					console.println("Supported commands:");
					console.println("\trepo use");
					console.println("\tissue list [number]");
					console.println("\tissue create");
					console.println("\tpull create");
					console.println("\tclear");
					console.println("\texit");
				} else if (line.trim().equals("clear")) {
					console.clearScreen();
				} else if (line.trim().length() > 0) {
					console.println("Command '" + line.trim() + "' nod found");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				TerminalFactory.get().restore();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
