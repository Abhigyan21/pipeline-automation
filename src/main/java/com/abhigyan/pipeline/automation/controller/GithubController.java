package com.abhigyan.pipeline.automation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.abhigyan.pipeline.automation.model.Repo;
import com.abhigyan.pipeline.automation.model.User;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONObject;

@RestController
public class GithubController {

	@Autowired
	private RestTemplate template;

	@Value("${access_token}")
	private String ACCESS_TOKEN;

	@PostMapping("/")
	public void getUserDetails(@RequestBody String payload) {
		DocumentContext jsonContext = JsonPath.parse(payload);

		User user = new User();
		user.setUserId(jsonContext.read("installation.account.id"));
		user.setLoginName(jsonContext.read("installation.account.login"));
		user.setRepoURL(jsonContext.read("installation.account.repos_url"));
		user.setRepoList(jsonContext.read("repositories[*].full_name"));

		user.toString();
		user.getRepoList().forEach(System.out::println);

		String branchesPayload = template
				.exchange("https://api.github.com/repos/Abhigyan21/pipeline-automation/git/refs/heads", HttpMethod.GET,
						null, String.class)
				.getBody();

		jsonContext = JsonPath.parse(branchesPayload);

		Repo repo = new Repo();

		repo.setAllBranches(jsonContext.read("[*].ref"));
		repo.setAllBranchesSHA(jsonContext.read("[*].object.sha"));

		repo.getAllBranches().forEach(System.out::println);
		repo.getAllBranchesSHA().forEach(System.out::println);

		// Below code will require OAuth authorization to work
		HttpHeaders header = new HttpHeaders();
		header.set("Authorization", "token " + ACCESS_TOKEN);
		header.setContentType(MediaType.APPLICATION_JSON);

		JSONObject body = new JSONObject();
		body.put("ref", "refs/heads/new_branch");
		body.put("sha", repo.getAllBranchesSHA().get(0));

		String newBranchCreationPayload = template
				.exchange("https://api.github.com/repos/Abhigyan21/pipeline-automation/git/refs", HttpMethod.POST,
						new HttpEntity<String>(body.toString(), header), String.class)
				.getBody();

		System.out.println("Pull URL");
		System.out.println(newBranchCreationPayload);
	}
}
